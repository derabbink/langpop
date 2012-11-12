package com.abbink.langpop.query.specific

import com.abbink.langpop.query.specific.github.GithubEventExtractorComponent
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractorComponent
import akka.actor.ActorRef

trait CombinedSpecificEventExtractorFactory {
	def createGithub(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
	def createStackoverflow(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait SingularSpecificEventExtractorFactory {
	def create(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait CombinedSpecificEventExtractorFactoryComponent {
	this :	GithubEventExtractorComponent with
			StackoverflowEventExtractorComponent =>
	
	def combinedSpecificEventExtractorFactory:CombinedSpecificEventExtractorFactory
	
	object CombinedSpecificEventExtractorFactoryImpl extends CombinedSpecificEventExtractorFactory {
		
		def createGithub(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor = {
			githubEventExtractorFactory.create(aggregator, beginTimestamp)
		}
		
		def createStackoverflow(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor = {
			stackoverflowEventExtractorFactory.create(aggregator, beginTimestamp)
		}
	}
}
