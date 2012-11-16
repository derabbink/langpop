package com.abbink.langpop.aggregate

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

object Aggregator {
	sealed trait AggregatorMessage
	//no messages that Aggregator can take are defined here
}

trait Aggregator {
	def init()
	
	def retrieve(tags:Set[String], timestamp:Long) : CombinedResponse
}

trait AggregatorComponent {
	this:	CombinedSpecificAggregatorFactoryComponent with
			QueryDependencyComponent =>
	
	def aggregator:Aggregator
	
	object AggregatorImpl extends Aggregator {
		println("Aggregator.<init>")
		private val config = ConfigFactory.load()
		private val mergedConfig = config.getConfig("langpop-aggregate").withFallback(config)
		private val tagsFileName = mergedConfig getString "langpop.aggregate.tagsfile"
		private val startTime:Long = mergedConfig.getLong("langpop.aggregate.starttime")
		
		private val system:ActorSystem = ActorSystem("LangpopSystem", mergedConfig)
		
		private var tags : Seq[String] = _
		private var githubAggregatorRef : ActorRef = _
		private var stackoverflowAggregatorRef : ActorRef = _
		
		def init() = {
			println("Aggregator.init()")
			tags = readTags(tagsFileName)
			startAggregators(tags, startTime)
			initQuerySystem(startTime)
		}
		
		private def readTags(tagsFile:String) : Seq[String] = {
			val timeout:Timeout = Timeout(10 seconds)
			var tagReaderRef = system.actorOf(Props[TagReader], name="TagReader")
			var f:Future[Seq[String]] = ask(tagReaderRef, TagReader.ReadFile(tagsFile))(timeout).mapTo[Seq[String]]
			Await.result[Seq[String]](f, timeout.duration)
		}
		
		private def startAggregators(tags:Seq[String], beginTimestamp:Long) = {
			//this may happen twice. the second time it will just silently fail
			try{
				githubAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createGithub(tags, beginTimestamp)), name = "GithubAggregator")
			}
			catch {
				case _ => githubAggregatorRef = system.actorFor("/user/GithubAggregator")
			}
			
			try {
				stackoverflowAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createStackoverflow(tags, beginTimestamp)), name = "StackoverflowAggregator")
			}
			catch {
				case _ => stackoverflowAggregatorRef = system.actorFor("/user/StackoverflowAggregator")
			}
		}
		
		private def initQuerySystem(startTime:Long) = {
			query.querySystem.init(system, githubAggregatorRef, stackoverflowAggregatorRef, startTime)
		}
		
		def retrieve(tags:Set[String], timestamp: Long) : CombinedResponse = {
			val msg = SpecificAggregator.Query(tags, timestamp)
			val timeout:Timeout = Timeout(3 seconds)
			
			val f1 = ask(githubAggregatorRef, msg)(timeout)
			val f2 = ask(stackoverflowAggregatorRef, msg)(timeout)
			//f1.flatMap(g => f2.flatMap(s => (g,s))).flatMap(r => CombinedResponse(tag, date, r._1.value.getOrElse[Long](0), r._2.value.getOrElse[Long](0))) //this doesn't work and is unreadable
			val f3 = for {
				github <- f1.mapTo[SpecificAggregator.QueryResponse]
				stackoverflow <- f2.mapTo[SpecificAggregator.QueryResponse]
			} yield (github.values, stackoverflow.values)
			
			val (git, stack) = Await.result(f3, timeout.duration).asInstanceOf[(Map[String, Long], Map[String, Long])]
			CombinedResponse(tags, timestamp, git, stack)
		}
	}
}
