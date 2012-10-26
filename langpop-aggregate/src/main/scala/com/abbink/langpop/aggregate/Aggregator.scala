package com.abbink.langpop.aggregate

import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import com.abbink.langpop.aggregate.tags.TagReader
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef

object Aggregator {
	
	sealed trait AggregatorMessage
	case class StartProcessing extends AggregatorMessage
	case class TagSeq(tags : Seq[String]) extends AggregatorMessage
	
	val config = ConfigFactory.parseString("""
			akka.loglevel = DEBUG
			akka.actor.debug {
				receive = on
				lifecycle = on
			}""")
	implicit val system:ActorSystem = ActorSystem("LangpopSystem", config)
	
	def start() = {
		var ref = system.actorOf(Props[Aggregator], name = "Aggregator")
		ref ! new StartProcessing
	}
}

class Aggregator extends Actor {
	import Aggregator._
	
	//var stackoverflowAggregatorRef : ActorRef
	//var githubAggregatorRef : ActorRef
	
	def receive = {
		case message : AggregatorMessage => message match {
			case _:StartProcessing =>
				context.actorOf(Props[TagReader], name="TagReader") ! TagReader.ReadFile("/tags.txt")
				
			case TagSeq(tags) =>
				//stackoverflowAggregatorRef = context.actorOf(Props[StackoverflowAggregator], name="StackoverflowAggregator")
				//githubAggregatorRef = context.actorOf(Props[GithubAggregator], name="GithubAggregator")
		}
	}
}
