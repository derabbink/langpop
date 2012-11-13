package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.StackoverflowEventExtractorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractor.Poll
import akka.actor.ActorRef
import akka.event.Logging
import com.typesafe.config.ConfigFactory

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
		
		//private var 
		
		override def preStart() = {
			log.debug("Starting StackoverflowEventExtractor")
		}
		
		override def receive = {
			case message : StackoverflowEventExtractorMessage => message match {
				case Poll() => 
			}
		}
		
		/**
		  * a blocking operation that queries the api
		  * /2.1/events
		  */
		private def poll() = {
			// /2.1/events?pagesize=100&site=stackoverflow&filter=!9hnGt*H(i
			// sort=creation (_date)
			// order=desc
		}
	}
}
