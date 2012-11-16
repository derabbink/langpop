package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.StackoverflowEventExtractorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.Poll
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Uri
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.UriParse
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Json
import com.abbink.langpop.query.specific.stackoverflow.Parser.EventsWrapper
import com.abbink.langpop.query.specific.stackoverflow.Parser.Event
import com.abbink.langpop.query.specific.stackoverflow.Parser.classofEventsWrapper
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Extracted
import scala.math._
import akka.actor.ActorRef
import akka.event.Logging
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import akka.routing.RoundRobinRouter
import akka.actor.Cancellable
import org.joda.time.DateTimeUtils
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import org.apache.http.client.utils.URIBuilder
import java.util.NavigableSet
import java.util.concurrent.ConcurrentSkipListSet
import java.util.Comparator
import akka.dispatch.Await
import akka.dispatch.Future
import net.liftweb.json.JsonAST.JValue

object StackoverflowEventExtractor {
	sealed trait StackoverflowEventExtractorMessage
	case class Poll extends StackoverflowEventExtractorMessage
}

trait StackoverflowEventExtractor extends SpecificEventExtractor {

}

trait StackoverflowEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait StackoverflowEventExtractorComponent {
	def stackoverflowEventExtractorFactory:StackoverflowEventExtractorFactory
	
	object StackoverflowEventExtractorFactoryImpl extends StackoverflowEventExtractorFactory {
		override def create(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : StackoverflowEventExtractor = {
			println("StackoverflowEventExtractorFactory.create()")
			new StackoverflowEventExtractorImpl(system, aggregator, beginTimestamp)
		}
	}
	
	/**
	  * this actor polls the API's /event feed every so often and then fires follow-up-requests for interesting events
	  */
	class StackoverflowEventExtractorImpl(override val system:ActorSystem, override val aggregator:ActorRef, val beginTimestamp:Long) extends SpecificEventExtractorImpl(system, aggregator) with StackoverflowEventExtractor {
		
		private val log = Logging(context.system, this)
		
		private val config = ConfigFactory.load()
		private val mergedConfig = config.getConfig("langpop-query").withFallback(config)
		
		private val queryInterval = mergedConfig.getInt("langpop.query.stackoverflow.eventQueryInterval")
		private val queryFilter = mergedConfig.getString("langpop.query.stackoverflow.filters.event")
		private val pageSize = 100
		private val site = "stackoverflow"
		
		private var currentTimestamp = beginTimestamp
		private var apiActorRef : ActorRef = _
		private var accessToken:String = _
		private var apiKey:String = _
		private var running : Boolean = false
		private var scheduled : Cancellable = _
		
		init()
		
		private def init() = {
			startChildren()
		}
		
		private def startChildren() = {
			//this could happen multiple times in a row. subsequent times silently fail
			try {
				apiActorRef = context.actorOf(Props[StackoverflowAPIActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name="StackoverflowAPIActor")
			}
		}
		
		protected def start(args:AnyRef) = {
			println("StackoverflowEventExtractorImpl.start()")
			running = true
			val args2 = args.asInstanceOf[(String, String)]
			accessToken = (args2 _1).asInstanceOf[String]
			apiKey = (args2 _2).asInstanceOf[String]
			val now : Long = DateTimeUtils.currentTimeMillis()/1000
			//set #currentTimestamp to no more than 10 seconds back (just to keep the first poll from being too large)
			currentTimestamp = max(currentTimestamp, now-10)
			//if currentTimestamp lies in the future
			val startDelay:Int = max(0, (currentTimestamp-now).toInt)
			schedule(startDelay)
		}
		
		/**
		  * schedules the next poll at a delay of #wait
		  */
		private def schedule(wait:Int) = {
			val from:Long = currentTimestamp
			
			scheduled = system.scheduler.scheduleOnce(wait seconds) {
				poll(from)
			}
		}
		
		protected def stop() = {
			if (running) {
				scheduled.cancel()
				//restart
				context.stop(apiActorRef)
				startChildren()
				
				running = false
			}
		}
		
		protected def isRunning() : Boolean = {
			running
		}
		
		override def preStart() = {
			log.debug("Starting StackoverflowEventExtractor")
		}
		
		/**
		  * a blocking operation that queries the API, if necessary across multiple pages
		  * for the time span of #from until now
		  * /2.1/events
		  */
		private def poll(from:Long) : Unit = {
			val now:Long = DateTimeUtils.currentTimeMillis()/1000
			// /2.1/events?pagesize=100&site=stackoverflow&filter=!9hnGt*H(i
			// /events cannot be ordered
			
			val events = getEvents(from)
			//TODO drop events newer than #now. those will be picked up in the next poll iteration
			
			//schedule next poll
			currentTimestamp = now + 1
			val newFrom = currentTimestamp
			val newNow:Long = DateTimeUtils.currentTimeMillis()/1000
			val waitCalc = (now - newNow).asInstanceOf[Int] + queryInterval //beginning time + interval - time *this* poll took
			val wait : Int = waitCalc<0 match {
				case true => 0
				case false => waitCalc
			}
			scheduled = system.scheduler.scheduleOnce(wait seconds) {
				poll(newFrom)
			}
		}
		
		/**
		  * produces a seq of event IDs in chronological order.
		  */
		private def getEvents(from:Long) : Seq[Long]= {
			import scala.collection.JavaConversions._
			//accumulative argument that collects all relevant events with (timestamp, event ID)
			//maintains chronological order
			var events : NavigableSet[(Long, Long)] = new ConcurrentSkipListSet[(Long, Long)](new TimestampEventComparator)
			traversePages(from, 1, events)
			
			//use events to generate result
			var eventIds : List[Long] = events.iterator().toList map (x => x _2)
			println("All events: "+ eventIds)
			//TODO
			null
		}
		
		/**
		  * Recursively query event pages, until no following page is found
		  * Events are always sorted youngest to oldest. No upper limit timestamp can be specified.
		  * Thus while querying the first page of events, new events might occur that push events from the first page down to the 2nd page, etc.
		  * Thus while navigating pages, the number of pages can increase.
		  * 
		  * The newly pushed-in events will be captured by the next iteration of the extractor.
		  */
		private def traversePages(from:Long, currentPage:Int, results:NavigableSet[(Long, Long)]) : Unit = {
			// https://api.stackexchange.com/2.1/events?pagesize=100&page=1&site=stackoverflow&since=1352897846&access_token=&key=&filter=!9hnGt*H(i
			val uriBuilder = new URIBuilder();
			uriBuilder.setScheme("https").setHost("api.stackexchange.com").setPath("/2.1/events")
				.setParameter("site", "stackoverflow")	
				.setParameter("since", from.toString())
				.setParameter("access_token", accessToken)
				.setParameter("key", apiKey)
				.setParameter("pagesize", "100")
				.setParameter("filter", queryFilter)
				.setParameter("page", currentPage.toString())
			val uri = uriBuilder.build
			
			val timeout:Timeout = Timeout(10 seconds)
			val f:Future[Extracted] = ask(apiActorRef, UriParse(classofEventsWrapper, uri))(timeout).mapTo[Extracted]
			val extracted = Await.result[Extracted](f, timeout.duration)
			val eventsw : Option[EventsWrapper] = extracted.data.asInstanceOf[Option[EventsWrapper]]
			
			println("parsed page: "+ eventsw)
			
			if (eventsw != None) {
				val data = eventsw.get
				collectEvents(from, data, results)
				continueTaversing(from, data, results)
			}
		}
		
		/**
		  * pulls all relevant events from #data and stores them in the accumulative argument #results
		  */
		private def collectEvents(from:Long, data:EventsWrapper, results:NavigableSet[(Long, Long)]) = {
			import scala.collection.JavaConversions._
			val items = data.items
					.filter (e => e match {
						case Event("question_posted", _, _) => false
						case Event("post_edited", _, _) => false
						case _ => true
					})
					.map (e => (e.creation_date, e.event_id))
			println("One page: "+items)
			results addAll items
		}
		
		/**
		  * read wrapper data and recursively traverse pages if needed
		  * does not respects backoff value
		  */
		private def continueTaversing(from:Long, data:EventsWrapper, results:NavigableSet[(Long, Long)]) = {
			// backoff:Option[Int], total:Int, page_size:Int, page:Int, `type`:String,
			// items:List[Event], quota_remaining:Int, quota_max:Int, has_more:Boolean
			if (data.has_more) {
				val totalPages = max(1, ceil(data.total / data.page_size).toInt)
				if (data.page < totalPages) {
					traversePages(from, data.page+1, results)
				}
			}
		}
		
		/**
		  * compares (a1:Long, a2:Long) to (b1:Long, b2:Long), such that first a1 is compared to b1, and if those equal, a2 is compared to b2
		  */
		private class TimestampEventComparator extends Comparator[(Long, Long)] {
			def compare(a:(Long,Long), b:(Long,Long)) : Int = {
				(a _1) compareTo (b _1) match {
					case 0 => (a _2) compareTo (b _2)
					case x => x
				}
			}
		}
	}
}
