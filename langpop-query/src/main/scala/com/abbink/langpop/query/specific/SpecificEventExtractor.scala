package com.abbink.langpop.query.specific

import akka.actor.Actor
import akka.event.Logging
import javax.management.Query
import com.abbink.langpop.query.specific.SpecificEventExtractor.SpecificEventExtractorMessage

object SpecificEventExtractor {
	sealed trait SpecificEventExtractorMessage
	//TODO define messages a SpecificEventExtractor actor can take
	
	sealed trait SpecificEventExtractorResponse
	case class AggregationResult(tag:String, timestamp:Long, number:Long) extends SpecificEventExtractorResponse
}

trait SpecificEventExtractor extends Actor {
	
}

/**
 * this has to be moved out of the SpecificEventExtractorComponent to be accessible for extension elsewhere
 */
abstract class SpecificEventExtractorImpl extends SpecificEventExtractor {
	
	private val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting SpecificAggregator")
	}
	
	def receive = {
		case message : SpecificEventExtractorMessage => message match {
			case _ => //no messages to implement
		}
	}
}
