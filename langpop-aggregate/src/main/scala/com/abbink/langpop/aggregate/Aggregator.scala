package com.abbink.langpop.aggregate

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import com.abbink.langpop.aggregate.specific.CombinedSpecificAggregatorFactoryComponent
import com.abbink.langpop.aggregate.specific.SpecificAggregator
import com.abbink.langpop.aggregate.tags.TagReader
import com.typesafe.config.ConfigFactory
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.dispatch.Await
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.duration.intToDurationInt
import akka.util.Timeout
import java.util.Formatter.DateTime

object Aggregator {
	sealed trait AggregatorMessage
	case class QueryResponse(value:Option[Long]) extends AggregatorMessage
}

trait Aggregator {
	def retrieve(tag: String, date: Date) : CombinedResponse
}

trait AggregatorComponent {
	this:	CombinedSpecificAggregatorFactoryComponent =>
	
	def aggregator:Aggregator
	
	object AggregatorImpl extends Aggregator {
		
		val config = ConfigFactory.load()
		val mergedConfig = config.getConfig("langpop-aggregate").withFallback(config)
		val tagsFileName = mergedConfig getString "langpop.aggregate.tagsfile"
		val startTime : Date = new Date(1000 * mergedConfig.getLong("langpop.aggregate.starttime"))
		
		var system:ActorSystem = ActorSystem("LangpopSystem", mergedConfig)
		
		private var tags : Seq[String] = _
		private var githubAggregatorRef : ActorRef = _
		private var stackoverflowAggregatorRef : ActorRef = _
		
		start()
		
		private def start() = {
			tags = readTags(tagsFileName)
			startAggregators(tags, startTime)
		}
		
		private def readTags(tagsFile:String) : Seq[String] = {
			val timeout:Timeout = Timeout(10 seconds)
			var tagReaderRef = system.actorOf(Props[TagReader], name="TagReader")
			var f:Future[Seq[String]] = ask(tagReaderRef, TagReader.ReadFile(tagsFile))(timeout).mapTo[Seq[String]]
			Await.result[Seq[String]](f, timeout.duration)
		}
		
		private def startAggregators(tags:Seq[String], beginDate:Date) {
			//this may happen twice. the second time it will just silently fail
			try{
				githubAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createGithubAggregator(tags, beginDate)), name = "GithubAggregator")
			}
			
			try {
				stackoverflowAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createStackoverflowAggregator(tags, beginDate)), name = "StackoverflowAggregator")
			}
		}
		
		def retrieve(tag: String, date: Date) : CombinedResponse = {
			val msg = SpecificAggregator.Query(tag, date)
			val timeout:Timeout = Timeout(3 seconds)
			
			val f1 = ask(githubAggregatorRef, msg)(timeout)
			val f2 = ask(stackoverflowAggregatorRef, msg)(timeout)
			//f1.flatMap(g => f2.flatMap(s => (g,s))).flatMap(r => CombinedResponse(tag, date, r._1.value.getOrElse[Long](0), r._2.value.getOrElse[Long](0))) //this doesn't work and is unreadable
			val f3 = for {
				github <- f1.mapTo[Aggregator.QueryResponse]
				stackoverflow <- f2.mapTo[Aggregator.QueryResponse]
			} yield (github.value.getOrElse[Long](0), stackoverflow.value.getOrElse[Long](0))
			
			val (git, stack) = Await.result(f3, timeout.duration).asInstanceOf[(Long, Long)]
			CombinedResponse(tag, date, git, stack)
		}
	}
}