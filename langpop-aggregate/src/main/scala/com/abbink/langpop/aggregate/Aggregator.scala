package com.abbink.langpop.aggregate

import java.util.Date
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregator
import com.abbink.langpop.aggregate.specific.SpecificAggregator
import com.abbink.langpop.aggregate.tags.TagReader
import com.typesafe.config.ConfigFactory
import Aggregator.AggregatorMessage
import Aggregator.CombinedResult
import Aggregator.Query
import Aggregator.QueryResult
import Aggregator.StartProcessing
import Aggregator.TagSeq
import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.dispatch.Await
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.Timeout.durationToTimeout
import akka.util.Duration
import akka.event.Logging

object Aggregator {
	
	sealed trait AggregatorMessage
	case class StartProcessing extends AggregatorMessage
	case class TagSeq(tags:Seq[String]) extends AggregatorMessage
	case class Query(tag:String, date:Date) extends AggregatorMessage
	case class QueryResult(tag:String, date:Date, number:Option[Long]) extends AggregatorMessage
	//this is a message only intended for outbound communication of the Aggregator actor
	case class CombinedResult(tag:String, date:Date, github:Option[Long], stackoverflow:Option[Long])
	
	implicit val system:ActorSystem = ActorSystem("LangpopSystem", ConfigFactory.load())
	
	private var aggregatorRef : ActorRef = _
	
	def start() = {
		aggregatorRef = system.actorOf(Props[Aggregator], name = "Aggregator")
		aggregatorRef ! StartProcessing
	}
	
	def retrieve(tag: String, date: Date) : CombinedResponse = {
		val f:Future[CombinedResult] = aggregatorRef.ask(Query(tag, date))(Duration.Inf).mapTo[CombinedResult]
		val CombinedResult(_, _, git, stack) = Await.result(f, Duration.Inf)
		
		var gitL:Long = git match {
			case None => 0
			case Some(n) => n
		}
		var stackL:Long = stack match {
			case None => 0
			case Some(n) => n
		}
		
		CombinedResponse(tag, date, gitL, stackL)
	}
}

class Aggregator extends Actor  {
	import Aggregator._
	import com.abbink.langpop.aggregate.specific.SpecificAggregator
	
	private var stackoverflowAggregatorRef : ActorRef = _
	private var githubAggregatorRef : ActorRef = _
	
	val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting")
	}
	
	def receive = {
		case message : AggregatorMessage => message match {
			case _:StartProcessing => startTagReader()
			case TagSeq(tags) => startSpecificAggregators(tags)
			case Query(tag, date) => forwardQueryToSpecificAggregators(tag, date)
		}
	}
	
	private def startTagReader () = {
		context.actorOf(Props[TagReader], name="TagReader") ! TagReader.ReadFile("/tags.txt")
	}
	
	private def startSpecificAggregators(tags : Seq[String]) = {
		stackoverflowAggregatorRef = context.actorOf(Props[StackoverflowAggregator], name="StackoverflowAggregator")
		githubAggregatorRef = context.actorOf(Props[GithubAggregator], name="GithubAggregator")
		
		val msg = SpecificAggregator.StartCrawling(tags, new Date(2012, 10, 25))
		stackoverflowAggregatorRef ! msg
		githubAggregatorRef ! msg
	}
	
	private def forwardQueryToSpecificAggregators(tag:String, date:Date) = {
		val msg = SpecificAggregator.Query(tag, date)
		val f1 = githubAggregatorRef.ask(msg)(Duration.Inf)
		val f2 = stackoverflowAggregatorRef.ask(msg)(Duration.Inf)
		
		val resultMsg = for {
			github <- f1.mapTo[QueryResult]
			stackoverflow <- f2.mapTo[QueryResult]
		} yield CombinedResult(tag, date, github.number, stackoverflow.number)
		
		sender ! resultMsg
	}
}
