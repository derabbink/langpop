package com.abbink.langpop.aggregate.specific.github

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import java.util.Date
import com.abbink.langpop.aggregate.specific.SingularSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import akka.event.Logging

trait GithubAggregator extends SpecificAggregator {
	
}

trait GithubAggregatorFactory extends SingularSpecificAggregatorFactory {
	override def create(tags:Seq[String], beginDate:Date) : SpecificAggregator	
}

trait GithubAggregatorComponent {
	
	def githubAggregatorFactory:GithubAggregatorFactory
	
	object GithubAggregatorFactoryImpl extends GithubAggregatorFactory {
		override def create(tags:Seq[String], beginDate:Date) : GithubAggregator = {
			println("  doing github 2")
			new GithubAggregatorImpl(tags, beginDate)
		}
	}
	
	class GithubAggregatorImpl(tags:Seq[String], beginDate:Date) extends SpecificAggregatorImpl(tags, beginDate) with GithubAggregator {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting GithubAggregator")
		}
	}
}
