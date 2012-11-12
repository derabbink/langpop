package com.abbink.langpop.aggregate.specific

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions.asScalaConcurrentMap
import scala.collection.mutable.ConcurrentMap
import SpecificAggregator.Query
import SpecificAggregator.SpecificAggregatorMessage
import akka.actor.Actor
import com.abbink.langpop.aggregate.Aggregator
import akka.event.Logging
import com.abbink.langpop.aggregate.Aggregator
import com.abbink.langpop.aggregate.Aggregator
import java.util.concurrent.ConcurrentSkipListMap
import java.util.SortedMap
import java.util.NavigableMap
import com.abbink.langpop.query.specific.SpecificEventExtractor.SpecificEventExtractorResponse
import com.abbink.langpop.query.specific.SpecificEventExtractor.AggregationResult

object SpecificAggregator {
	sealed trait SpecificAggregatorMessage
	case class Query(tags:Set[String], timestamp:Long) extends SpecificAggregatorMessage
	
	sealed trait SpecificAggregatorResponse
	case class QueryResponse(values:Map[String, Long]) extends SpecificAggregatorResponse
}

trait SpecificAggregator extends Actor {
	
	protected def query(tags:Set[String], timestamp:Long) : SpecificAggregator.QueryResponse
	
	protected def processAggregationResult(tag:String, timestamp:Long, number:Long)
}

/**
  * this has to be moved out of the SpecificAggregatorComponent to be accessible for extension elsewhere
  */
abstract class SpecificAggregatorImpl(val tags:Seq[String], val beginTimestamp:Long) extends SpecificAggregator {
	
	private var store : NavigableMap[Long, Map[String, Long]] = new ConcurrentSkipListMap[Long, Map[String, Long]]
	
	private val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting SpecificAggregator")
	}
	
	def receive = {
		case message : SpecificAggregatorMessage => message match {
			case Query(tags, timestamp) => sender ! query(tags, timestamp)
		}
		case message : SpecificEventExtractorResponse => message match {
			case AggregationResult(tag, timestamp, number) => processAggregationResult(tag, timestamp, number)
		}
	}
	
	protected def query(tags:Set[String], timestamp:Long) : SpecificAggregator.QueryResponse = {
		val answers:Map[String, Long] = queryRecursive(tags, timestamp)
		SpecificAggregator.QueryResponse(answers)
	}
	
	/**
	  * recursive lookup of tags in virtual time frames
	  */
	private def queryRecursive(tags:Set[String], timestamp:Long) : Map[String, Long] = {
		if (timestamp > beginTimestamp) {
			val floor:java.util.Map.Entry[Long, Map[String, Long]] = store.floorEntry(timestamp)
			if (floor != null) {
				val found:Map[String, Long] = floor.getValue() filter (pair => tags contains (pair _1))
				val remaining = tags -- (found map (pair => pair _1))
				found ++ queryRecursive(remaining, floor.getKey()-1)
			}
			else
				Map()
		}
		else
			Map()
	}
	
	protected def processAggregationResult(tag:String, timestamp:Long, number:Long) {
		var writeBack = false
		var metrics:Map[String, Long] = store.get(timestamp) match {
			case null => writeBack = true
					Map()
			case m => m
		}
		
		metrics += (tag -> number)
		
		if (writeBack)
			store.put(timestamp, metrics)
	}
}


trait SpecificAggregatorComponent {
	
}
