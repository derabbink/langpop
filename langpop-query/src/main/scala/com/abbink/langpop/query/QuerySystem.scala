package com.abbink.langpop.query

import com.abbink.langpop.query.specific.CombinedSpecificEventExtractorFactoryComponent
import com.abbink.langpop.query.specific.SpecificEventExtractor.Start
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

object QuerySystem {

}

trait QuerySystem {
	def init(actorSystem:ActorSystem, githubActorRef:ActorRef, stackoverflowActorRef:ActorRef, startTimestamp:Long)
	
	def startStackOverflow(accessToken:String)
	def startGithub()
	
	def stopStackOverflow()
	def stopGithub()
	
	def isRunningStackOverflow() : Boolean
	def isRunningGithub() : Boolean
	
	def requestCountStackOverflow() : Int
	def requestCountGithub() : Int
}

trait QuerySystemComponent {
	this : CombinedSpecificEventExtractorFactoryComponent =>
	
	def querySystem:QuerySystem
	
	object QuerySystemImpl extends QuerySystem {
		println("QuerySystem.<init>")
		private val config = ConfigFactory.load()
		private val mergedConfig = config.getConfig("langpop-query").withFallback(config)
		
		private var system : ActorSystem = _
		private var startTimestamp : Long = _
		private var githubActorRef : ActorRef = _
		private var githubExtractorActorRef : ActorRef = _
		private var stackoverflowActorRef : ActorRef = _
		private var stackoverflowExtractorActorRef : ActorRef = _
		
		def init(actorSystem:ActorSystem, githubActorRef:ActorRef, stackoverflowActorRef:ActorRef, startTimestamp:Long) = {
			println("QuerySystem.init()")
			system = actorSystem
			this.startTimestamp = startTimestamp
			initGithub()
			initStackOverflow()
		}
		
		private def initGithub() = {}
		
		private def initStackOverflow() = {
			//this may happen twice. the second time it will just silently fail
			try{
				stackoverflowExtractorActorRef = system.actorOf(Props(combinedSpecificEventExtractorFactory.createStackoverflow(system, stackoverflowActorRef, startTimestamp)), name = "StackoverflowEventExtractor")
			}
			catch {
				case _ => stackoverflowExtractorActorRef = system.actorFor("/user/StackoverflowEventExtractor")
			}
		}
		
		def startStackOverflow(accessToken:String) = {
			//sometimes the actor ref is not initialized
			initStackOverflow()
			stackoverflowExtractorActorRef ! Start(accessToken)
		}
		
		def startGithub() = {}
		
		def stopStackOverflow() = {
			//TODO
		}
		
		def stopGithub() = {}
		
		def isRunningStackOverflow() : Boolean = {
			//TODO
			false
		}
		
		def isRunningGithub() : Boolean = {
			false
		}
		
		def requestCountStackOverflow() : Int = {
			//TODO
			0
		}
		
		def requestCountGithub() : Int = {
			0
		}
	}
}
