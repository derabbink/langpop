package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.StackoverflowEventExtractorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.Poll
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Uri
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Json
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
		
		protected def start(args:AnyRef*) = {
			running = true
			accessToken = args(0).asInstanceOf[String]
			apiKey = args(1).asInstanceOf[String]
			schedule(0)
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
			//TODO drop events newer than #now
			
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
			//accumulative argument that collects all relevant events with (timestamp, event ID)
			//maintains chronological order
			var events : NavigableSet[(Long, Long)] = new ConcurrentSkipListSet[(Long, Long)](new TimestampEventComparator)
			traversePages(from, 1, events)
			
			//TODO use #events to generate result
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
		private def traversePages(from:Long, currentPage:Int, results:NavigableSet[(Long, Long)]) = {
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
			val f:Future[Json] = ask(apiActorRef, Uri(uri))(timeout).mapTo[Json]
			val json = Await.result[Json](f, timeout.duration)
			
			if (json.data != None) {
				val data = json.data.get
				collectEvents(from, data, results)
				
				//TODO recursively go to next page if it exists
			}
		}
		
		/**
		  * pulls all relevant events from #data and stores them in the accumulative argument #results
		  */
		private def collectEvents(from:Long, data:JValue, results:NavigableSet[(Long, Long)]) = {
			//TODO
			null
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
