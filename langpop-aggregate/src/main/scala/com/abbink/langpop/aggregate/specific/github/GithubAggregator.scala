package com.abbink.langpop.aggregate.specific.github

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import com.abbink.langpop.aggregate.specific.SingularSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import akka.event.Logging

trait GithubAggregator extends SpecificAggregator {
	
}

trait GithubAggregatorFactory extends SingularSpecificAggregatorFactory {
	override def create(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator	
}

trait GithubAggregatorComponent {
	
	def githubAggregatorFactory:GithubAggregatorFactory
	
	object GithubAggregatorFactoryImpl extends GithubAggregatorFactory {
		override def create(tags:Seq[String], beginTimestamp:Long) : GithubAggregator = {
			new GithubAggregatorImpl(tags, beginTimestamp)
		}
	}
	
	class GithubAggregatorImpl(tags:Seq[String], beginTimestamp:Long) extends SpecificAggregatorImpl(tags, beginTimestamp) with GithubAggregator {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting GithubAggregator")
		}
	}
}
