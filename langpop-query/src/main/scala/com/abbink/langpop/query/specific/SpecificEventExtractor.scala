package com.abbink.langpop.query.specific

import akka.actor.Actor
import akka.event.Logging
import javax.management.Query
import com.abbink.langpop.query.specific.SpecificEventExtractor.SpecificEventExtractorControlMessage
import com.abbink.langpop.query.specific.SpecificEventExtractor.Start
import com.abbink.langpop.query.specific.SpecificEventExtractor.Stop
import com.abbink.langpop.query.specific.SpecificEventExtractor.AskRunning
import com.abbink.langpop.query.specific.SpecificEventExtractor.SpecificEventExtractorControlResponseMessage
import com.abbink.langpop.query.specific.SpecificEventExtractor.Running
import akka.actor.ActorRef
import akka.actor.ActorSystem

object SpecificEventExtractor {
	sealed trait SpecificEventExtractorControlMessage
	case class Start(args:AnyRef) extends SpecificEventExtractorControlMessage
	case class Stop extends SpecificEventExtractorControlMessage
	case class AskRunning extends SpecificEventExtractorControlMessage
	
	sealed trait SpecificEventExtractorControlResponseMessage
	case class Running(running:Boolean) extends SpecificEventExtractorControlResponseMessage
	
	sealed trait SpecificEventExtractorResponse
	case class AggregationResult(tag:String, timestamp:Long, number:Long) extends SpecificEventExtractorResponse
}

trait SpecificEventExtractor extends Actor {
	protected def start(args:AnyRef)
	
	protected def stop()
	
	protected def isRunning() : Boolean
}

/**
  * this has to be moved out of the SpecificEventExtractorComponent to be accessible for extension elsewhere
  */
abstract class SpecificEventExtractorImpl(val system:ActorSystem, val aggregator:ActorRef) extends SpecificEventExtractor {
	
	private val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting SpecificAggregator")
	}
	
	def receive = {
		case message : SpecificEventExtractorControlMessage => message match {
			case Start(args) =>
				println("SpecificEventExtractor.receive Start()")
				start(args)
			case Stop() => stop()
			case AskRunning() => sender ! Running(isRunning())
		}
		case m => println("SpecificEventExtractor.received unknown: "+m.toString())
	}
}
