package com.abbink.langpop.aggregate.specific

import java.util.concurrent.ConcurrentHashMap
import java.util.Date
import scala.collection.JavaConversions.asScalaConcurrentMap
import scala.collection.mutable.ConcurrentMap
import SpecificAggregator.AggregationResult
import SpecificAggregator.Query
import SpecificAggregator.SpecificAggregatorMessage
import SpecificAggregator.StartCrawling
import akka.actor.Actor
import com.abbink.langpop.aggregate.Aggregator
import akka.event.Logging

object SpecificAggregator {
	
	sealed trait SpecificAggregatorMessage
	case class StartCrawling(tags:Seq[String], beginDate:Date) extends SpecificAggregatorMessage
	case class Query(tag:String, date:Date) extends SpecificAggregatorMessage
	case class AggregationResult(tag:String, date:Date, number:Long) extends SpecificAggregatorMessage
	
}

case class TagDate(tag:String, date:Date)

trait SpecificAggregator extends Actor {
	import SpecificAggregator._
	
	protected var tags : Seq[String] = _
	protected var beginDate : Date = _
	protected var store : ConcurrentMap[TagDate, Long] = new ConcurrentHashMap[TagDate, Long]
	
	val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting")
	}
	
	def receive = {
		case message : SpecificAggregatorMessage => message match {
			case StartCrawling(tags, date) => startCrawling(tags, date)
			case q:Query => query(q)
			case r:AggregationResult => processAggregationResult(r)
		}
	}
	
	private def startCrawling(tags : Seq[String], beginDate:Date) = {
		this.tags = tags
		this.beginDate = beginDate
		startActors()
	}
	
	protected def startActors()
	
	private def query(query : Query) = {
		import Aggregator._
		
		import query._
		var key = TagDate(tag, date)
		var num : Option[Long] = store get key
		
		sender ! QueryResult(tag, date, num)
	}
	
	private def processAggregationResult(r : AggregationResult) {
		//TODO
	}
}
