package com.abbink.langpop.query.specific

import com.abbink.langpop.query.specific.github.GithubEventExtractorComponent
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractorComponent
import akka.actor.ActorRef
import akka.actor.ActorSystem

trait CombinedSpecificEventExtractorFactory {
	def createGithub(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
	def createStackoverflow(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait SingularSpecificEventExtractorFactory {
	def create(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait CombinedSpecificEventExtractorFactoryComponent {
	this :	GithubEventExtractorComponent with
			StackoverflowEventExtractorComponent =>
	
	def combinedSpecificEventExtractorFactory:CombinedSpecificEventExtractorFactory
	
	object CombinedSpecificEventExtractorFactoryImpl extends CombinedSpecificEventExtractorFactory {
		
		def createGithub(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor = {
			githubEventExtractorFactory.create(system, aggregator, beginTimestamp)
		}
		
		def createStackoverflow(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor = {
			println("CombinedSpecificEventExtractorFactory.createStackoverflow()")
			stackoverflowEventExtractorFactory.create(system, aggregator, beginTimestamp)
		}
	}
}
