package com.abbink.langpop.aggregate.specific

import com.abbink.langpop.aggregate.specific.github.GithubAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorFactory
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorFactory

trait CombinedSpecificAggregatorFactory {
	def createGithub(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator
	def createStackoverflow(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator
}

trait SingularSpecificAggregatorFactory {
	def create(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator
}

trait CombinedSpecificAggregatorFactoryComponent {
	this :	GithubAggregatorComponent with
			StackoverflowAggregatorComponent =>
	def combinedSpecificAggregatorFactory:CombinedSpecificAggregatorFactory
	
	object CombinedSpecificAggregatorFactoryImpl extends CombinedSpecificAggregatorFactory {
		
		def createGithub(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator = {
			githubAggregatorFactory.create(tags, beginTimestamp)
		}
		
		def createStackoverflow(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator = {
			stackoverflowAggregatorFactory.create(tags, beginTimestamp)
		}
	}
}
