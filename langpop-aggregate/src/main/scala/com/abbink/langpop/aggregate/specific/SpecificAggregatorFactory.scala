package com.abbink.langpop.aggregate.specific

import com.abbink.langpop.aggregate.specific.github.GithubAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorComponent
import java.util.Date
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorFactory
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorFactory

trait CombinedSpecificAggregatorFactory {
	def createGithubAggregator(tags:Seq[String], beginDate:Date) : SpecificAggregator
	def createStackoverflowAggregator(tags:Seq[String], beginDate:Date) : SpecificAggregator
}

trait SingularSpecificAggregatorFactory {
	def create(tags:Seq[String], beginDate:Date) : SpecificAggregator
}

trait CombinedSpecificAggregatorFactoryComponent {
	this :	GithubAggregatorComponent with
			StackoverflowAggregatorComponent =>
	val combinedSpecificAggregatorFactory:CombinedSpecificAggregatorFactory
	
	object CombinedSpecificAggregatorFactoryImpl extends CombinedSpecificAggregatorFactory {
		
		def createGithubAggregator(tags:Seq[String], beginDate:Date) : SpecificAggregator = {
			println("  doing github 1")
			githubAggregatorFactory.create(tags, beginDate)
		}
		
		def createStackoverflowAggregator(tags:Seq[String], beginDate:Date) : SpecificAggregator = {
			println("  doing stackoverflow 1")
			stackoverflowAggregatorFactory.create(tags, beginDate)
		}
	}
}
