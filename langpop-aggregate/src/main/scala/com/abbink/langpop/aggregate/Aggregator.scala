package com.abbink.langpop.aggregate

import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.pattern.ask
import com.abbink.langpop.aggregate.tags.TagReader
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import java.util.Date
import akka.dispatch.Await
import akka.dispatch.Future
import akka.util.Timeout
import akka.util.Duration
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregator

object Aggregator {
	
	sealed trait AggregatorMessage
	case class StartProcessing extends AggregatorMessage
	case class TagSeq(tags:Seq[String]) extends AggregatorMessage
	case class Query(tag:String, date:Date) extends AggregatorMessage
	//this is a message only intended for outbound communication of the Aggregator actor
	case class QueryResult(tag:String, date:Date, github:Option[Long], stackoverflow:Option[Long])
	
	implicit val system:ActorSystem = ActorSystem("LangpopSystem", ConfigFactory.load())
	
	private var aggregatorRef : ActorRef = _
	
	def start() = {
		aggregatorRef = system.actorOf(Props[Aggregator], name = "Aggregator")
		aggregatorRef ! StartProcessing
	}
	
	def retrieve(tag: String, date: Date) : CombinedResponse = {
		var f:Future[QueryResult] = aggregatorRef.ask(Query(tag, date))(Duration.Inf).mapTo[QueryResult]
		var QueryResult(_, _, git, stack) = Await.result(f, Duration.Inf)
		
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
		githubAggregatorRef ! msg
		stackoverflowAggregatorRef ! msg
	}
}
