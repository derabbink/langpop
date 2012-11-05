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

object SpecificAggregator {
	
	sealed trait SpecificAggregatorMessage
	case class Query(tag:String, date:Date) extends SpecificAggregatorMessage
	case class AggregationResult(tag:String, date:Date, number:Long) extends SpecificAggregatorMessage
	
}

case class TagDate(tag:String, date:Date)

abstract class SpecificAggregator(tags:Seq[String], beginDate:Date) extends Actor {
	import SpecificAggregator._
	
	protected var beginDate : Date = _
	protected var store : ConcurrentMap[TagDate, Long] = new ConcurrentHashMap[TagDate, Long]
	
	val log = Logging(context.system, this)
	
	startCrawling()
	
	override def preStart() = {
		log.debug("Starting SpecificAggregator")
	}
	
	def receive = {
		case message : SpecificAggregatorMessage => message match {
			case q:Query => query(q)
			case r:AggregationResult => processAggregationResult(r)
		}
	}
	
	private def startCrawling() = {
		startActors()
	}
	
	protected def startActors()
	
	private def query(query : Query) = {
		import Aggregator._
		
		import query._
		var key = TagDate(tag, date)
		var num : Option[Long] = store get key
		
		sender ! Aggregator.QueryResponse(num)
	}
	
	private def processAggregationResult(r : AggregationResult) {
		//TODO
	}
}
