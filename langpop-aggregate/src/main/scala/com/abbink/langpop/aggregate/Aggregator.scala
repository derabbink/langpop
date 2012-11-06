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

object Aggregator {
	sealed trait AggregatorMessage
	case class QueryResponse(value:Option[Long]) extends AggregatorMessage
}

trait Aggregator {
	def start()
	
	protected def readTags(tagsFile:String) : Seq[String]
	
	protected def startAggregators(tags:Seq[String], beginDate:Date)
	
	def retrieve(tag: String, date: Date) : CombinedResponse
}

trait AggregatorComponent {
	this:	CombinedSpecificAggregatorFactoryComponent =>
	
	val aggregator:Aggregator
	
	object AggregatorImpl extends Aggregator {
		
		val config = ConfigFactory.load()
		val mergedConfig = config.getConfig("langpop-aggregate").withFallback(config)
		implicit val system:ActorSystem = ActorSystem("LangpopSystem", mergedConfig)
		
		protected var githubAggregatorRef : ActorRef = _
		protected var stackoverflowAggregatorRef : ActorRef = _
		
		println(" we have a NEW INSTANCE")
		try {
			throw new Exception()
		} catch {
			case e:Exception =>
				var m:String = "-"+e.getMessage()
				var s:String = "-"+e.getStackTraceString
				println(m)
				println(s)
			case a => println("-we've got something else: "+a)
		}
		
		def start() = {
			val tagsFile = mergedConfig getString "langpop.aggregate.tagsfile"
			val format:DateFormat = new SimpleDateFormat("yyyy-MM-dd")
			val startDate = format parse (mergedConfig getString "langpop.aggregate.startdate")
			
			var tags = readTags(tagsFile)
			startAggregators(tags, startDate)
		}
		
		protected def readTags(tagsFile:String) : Seq[String] = {
			val timeout:Timeout = Timeout(10 seconds)
			var tagReaderRef = system.actorOf(Props[TagReader], name="TagReader")
			var f:Future[Seq[String]] = ask(tagReaderRef, TagReader.ReadFile(tagsFile))(timeout).mapTo[Seq[String]]
			Await.result[Seq[String]](f, timeout.duration)
		}
		
		protected def startAggregators(tags:Seq[String], beginDate:Date) {
			println("  setting Aggregators")
			githubAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createGithubAggregator(tags, beginDate)), name = "GithubAggregator")
			stackoverflowAggregatorRef = system.actorOf(Props(combinedSpecificAggregatorFactory.createStackoverflowAggregator(tags, beginDate)), name = "StackoverflowAggregator")
		}
		
		def retrieve(tag: String, date: Date) : CombinedResponse = {
			val msg = SpecificAggregator.Query(tag, date)
			val timeout:Timeout = Timeout(3 seconds)
			
			println("   asking aggregators")
			if (null == githubAggregatorRef)
				println("   we lost github")
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