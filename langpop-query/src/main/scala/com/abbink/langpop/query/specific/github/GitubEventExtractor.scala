package com.abbink.langpop.query.specific.github

import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import akka.event.Logging
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import akka.actor.ActorRef

trait GithubEventExtractor extends SpecificEventExtractor {

}

trait GithubEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait GithubEventExtractorComponent {
	def githubEventExtractorFactory:GithubEventExtractorFactory
	
	object GithubEventExtractorFactoryImpl extends GithubEventExtractorFactory {
		override def create(aggregator:ActorRef, beginTimestamp:Long) : GithubEventExtractor = {
			new GithubEventExtractorImpl(aggregator, beginTimestamp)
		}
	}
	
	class GithubEventExtractorImpl(override val aggregator:ActorRef, val beginTimestamp:Long) extends SpecificEventExtractorImpl(aggregator) with GithubEventExtractor {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting GithubEventExtractor")
		}
	}
}
