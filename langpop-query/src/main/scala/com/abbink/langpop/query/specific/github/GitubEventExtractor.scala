package com.abbink.langpop.query.specific.github

import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import akka.event.Logging
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl

trait GithubEventExtractor extends SpecificEventExtractor {

}

trait GithubEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(beginTimestamp:Long) : SpecificEventExtractor
}

trait GithubEventExtractorComponent {
	def githubEventExtractorFactory:GithubEventExtractorFactory
	
	object GithubEventExtractorFactoryImpl extends GithubEventExtractorFactory {
		override def create(beginTimestamp:Long) : GithubEventExtractor = {
			new GithubEventExtractorImpl(beginTimestamp)
		}
	}
	
	class GithubEventExtractorImpl(val beginTimestamp:Long) extends SpecificEventExtractorImpl with GithubEventExtractor {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting GithubEventExtractor")
		}
	}
}
