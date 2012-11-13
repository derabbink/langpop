package com.abbink.langpop.query

import com.abbink.langpop.query.specific.CombinedSpecificEventExtractorFactoryComponent
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

object QuerySystem {

}

trait QuerySystem {
	def startStackOverflow()
	def startGithub()
	
	def stopStackOverflow()
	def stopGithub()
}

trait QuerySystemComponent {
	this : CombinedSpecificEventExtractorFactoryComponent =>
	
	def querySystem:QuerySystem
	
	object QuerySystemImpl extends QuerySystem {
		
		private val config = ConfigFactory.load()
		private val mergedConfig = config.getConfig("langpop-query").withFallback(config)
		
		private var system : ActorSystem = _
		
		private def init(actorSystem : ActorSystem) = {
			system = actorSystem
		}
		
		def startStackOverflow() = {
			
		}
		
		def startGithub() = {}
		
		def stopStackOverflow() = {
			
		}
		
		def stopGithub() = {}
	}
}
