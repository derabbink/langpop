package com.abbink.langpop.query.specific

import com.abbink.langpop.query.specific.github.GithubEventExtractorComponent
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractorComponent

trait CombinedSpecificEventExtractorFactory {
	def createGithub(beginTimestamp:Long) : SpecificEventExtractor
	def createStackoverflow(beginTimestamp:Long) : SpecificEventExtractor
}

trait SingularSpecificEventExtractorFactory {
	def create(beginTimestamp:Long) : SpecificEventExtractor
}

trait CombinedSpecificEventExtractorFactoryComponent {
	this :	GithubEventExtractorComponent with
			StackoverflowEventExtractorComponent =>
	
	def combinedSpecificEventExtractorFactory:CombinedSpecificEventExtractorFactory
	
	object CombinedSpecificEventExtractorFactoryImpl extends CombinedSpecificEventExtractorFactory {
		
		def createGithub(beginTimestamp:Long) : SpecificEventExtractor = {
			githubEventExtractorFactory.create(beginTimestamp)
		}
		
		def createStackoverflow(beginTimestamp:Long) : SpecificEventExtractor = {
			stackoverflowEventExtractorFactory.create(beginTimestamp)
		}
	}
}
