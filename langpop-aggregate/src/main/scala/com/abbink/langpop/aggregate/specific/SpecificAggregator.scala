package com.abbink.langpop.aggregate.specific

import java.util.concurrent.ConcurrentHashMap
import java.util.Date
import scala.collection.JavaConversions.asScalaConcurrentMap
import scala.collection.mutable.ConcurrentMap
import SpecificAggregator.AggregationResult
import SpecificAggregator.Query
import SpecificAggregator.SpecificAggregatorMessage
import akka.actor.Actor
import com.abbink.langpop.aggregate.Aggregator
import akka.event.Logging
import com.abbink.langpop.aggregate.Aggregator
import com.abbink.langpop.aggregate.Aggregator

object SpecificAggregator {
	sealed trait SpecificAggregatorMessage
	case class Query(tag:String, date:Date) extends SpecificAggregatorMessage
	case class AggregationResult(tag:String, date:Date, number:Long) extends SpecificAggregatorMessage
}

trait SpecificAggregator extends Actor {
	
	case class TagDate(tag:String, date:Date)
	
	protected def startActors()
	
	protected def query(tag:String, date:Date) : Aggregator.QueryResponse
	
	protected def processAggregationResult(tag:String, date:Date, number:Long)
}

/**
 * this has to be moved out of the SpecificAggregatorComponent to be accessible for extension elsewhere
 */
abstract class SpecificAggregatorImpl(tags:Seq[String], beginDate:Date) extends SpecificAggregator {
	
	protected var store : ConcurrentMap[TagDate, Long] = new ConcurrentHashMap[TagDate, Long]
	
	protected val log = Logging(context.system, this)
	
	startActors()
	
	override def preStart() = {
		log.debug("Starting SpecificAggregator")
	}
	
	def receive = {
		case message : SpecificAggregatorMessage => message match {
			case Query(tag, date) => sender ! query(tag, date)
			case AggregationResult(tag, date, number) => processAggregationResult(tag, date, number)
		}
	}
	
	protected def query(tag:String, date:Date) : Aggregator.QueryResponse = {
		val key = TagDate(tag, date)
		val num : Option[Long] = store get key
		Aggregator.QueryResponse(num)
	}
	
	protected def processAggregationResult(tag:String, date:Date, number:Long) {
		val key = TagDate(tag, date)
		store.put(key, number)
	}
}


trait SpecificAggregatorComponent {
	
}
