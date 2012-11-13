package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.StackoverflowEventExtractorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.Poll
import akka.actor.ActorRef
import akka.event.Logging
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import akka.routing.RoundRobinRouter

object StackoverflowEventExtractor {
	sealed trait StackoverflowEventExtractorMessage
	case class Poll extends StackoverflowEventExtractorMessage
}

trait StackoverflowEventExtractor extends SpecificEventExtractor {

}

trait StackoverflowEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait StackoverflowEventExtractorComponent {
	def stackoverflowEventExtractorFactory:StackoverflowEventExtractorFactory
	
	object StackoverflowEventExtractorFactoryImpl extends StackoverflowEventExtractorFactory {
		override def create(aggregator:ActorRef, beginTimestamp:Long) : StackoverflowEventExtractor = {
			new StackoverflowEventExtractorImpl(aggregator, beginTimestamp)
		}
	}
	
	/**
	  * this actor polls the API's /event feed every so often and then fires follow-up-requests for interesting events
	  */
	class StackoverflowEventExtractorImpl(override val aggregator:ActorRef, val beginTimestamp:Long) extends SpecificEventExtractorImpl(aggregator) with StackoverflowEventExtractor {
		
		private val log = Logging(context.system, this)
		
		private val config = ConfigFactory.load()
		private val mergedConfig = config.getConfig("langpop-query").withFallback(config)
		
		private val queryInterval = mergedConfig.getInt("langpop.query.stackoverflow.eventQueryInterval")
		private val queryFilter = mergedConfig.getInt("langpop.query.stackoverflow.filters.event")
		private val pageSize = 100
		private val site = "stackoverflow"
		
		private var currentTimestamp = beginTimestamp
		private var apiActorRef : ActorRef = _
		private var accessToken:String = _
		
		init()
		
		private def init() = {
			startChildren()
		}
		
		private def startChildren() = {
			apiActorRef = context.actorOf(Props[StackoverflowAPIActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name="StackoverflowAPIActor")
		}
		
		def start(args:AnyRef*) = {
			accessToken = args(0).asInstanceOf[String]
			//TODO initialize scheduler
		}
		
		override def preStart() = {
			log.debug("Starting StackoverflowEventExtractor")
		}
		
		override def receive = {
			case message : StackoverflowEventExtractorMessage => message match {
				case Poll() => poll()
			}
		}
		
		/**
		  * a blocking operation that queries the API, if necessary across multiple pages
		  * /2.1/events
		  */
		private def poll() = {
			
			// /2.1/events?pagesize=100&site=stackoverflow&filter=!9hnGt*H(i
			// sort=creation (_date)
			// order=desc
		}
	}
}
