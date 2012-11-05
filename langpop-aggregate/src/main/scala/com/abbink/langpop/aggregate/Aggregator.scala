package com.abbink.langpop.aggregate

import java.util.Date
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregator
import com.abbink.langpop.aggregate.specific.SpecificAggregator
import com.abbink.langpop.aggregate.tags.TagReader
import com.typesafe.config.ConfigFactory
import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.dispatch.Await
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Duration
import akka.util.duration._
import akka.event.Logging
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.management.Query

object Aggregator {
	
	sealed trait AggregatorMessage
	case class QueryResponse(value:Option[Long]) extends AggregatorMessage
	
	val config = ConfigFactory.load()
	val mergedConfig = config.getConfig("langpop-aggregate").withFallback(config)
	implicit val system:ActorSystem = ActorSystem("LangpopSystem", mergedConfig)
	
	private var githubAggregatorRef : ActorRef = _
	private var stackoverflowAggregatorRef : ActorRef = _
	
	def start() = {
		val tagsFile = mergedConfig getString "langpop.aggregate.tagsfile"
		val format:DateFormat = new SimpleDateFormat("yyyy-MM-dd")
		val startDate = format parse (mergedConfig getString "langpop.aggregate.startdate")
		
		var tags = readTags(tagsFile)
		startAggregators(tags, startDate)
	}
	
	private def readTags(tagsFile:String) : Seq[String] = {
		val timeout:Timeout = Timeout(10 seconds)
		var tagReaderRef = system.actorOf(Props[TagReader], name="TagReader")
		var f:Future[Seq[String]] = ask(tagReaderRef, TagReader.ReadFile(tagsFile))(timeout).mapTo[Seq[String]]
		Await.result[Seq[String]](f, timeout.duration)
	}
	
	private def startAggregators(tags:Seq[String], beginDate:Date) {
		githubAggregatorRef = system.actorOf(Props(new GithubAggregator(tags, beginDate)), name = "GithubAggregator")
		stackoverflowAggregatorRef = system.actorOf(Props(new StackoverflowAggregator(tags, beginDate)), name = "StackoverflowAggregator")
	}
	
	def retrieve(tag: String, date: Date) : CombinedResponse = {
		val msg = SpecificAggregator.Query(tag, date)
		val timeout:Timeout = Timeout(3 seconds)
		
		val f1 = ask(githubAggregatorRef, msg)(timeout)
		val f2 = ask(stackoverflowAggregatorRef, msg)(timeout)
		val f3 = for {
			github <- f1.mapTo[QueryResponse]
			stackoverflow <- f2.mapTo[QueryResponse]
			
			combined <- (Future {
				val git = github.value.getOrElse[Long](0)
				val stack = stackoverflow.value.getOrElse[Long](0)
				CombinedResponse(tag, date, git, stack)
			}).mapTo[CombinedResponse]
		} yield combined
		
		Await.result(f3, timeout.duration).asInstanceOf[CombinedResponse]
	}
}