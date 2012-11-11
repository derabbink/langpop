package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import akka.event.Logging

object StackoverflowAggregatorFactoryTestImpl extends StackoverflowAggregatorFactory {
	override def create(tags:Seq[String], beginTimestamp:Long) : GithubAggregator = {
		new GithubAggregatorTestImpl(tags, beginTimestamp)
	}
}

class GithubAggregatorTestImpl(tags:Seq[String], beginTimestamp:Long) extends SpecificAggregatorImpl(tags, beginTimestamp) with GithubAggregator {
	
	private val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting GithubAggregatorTestImpl")
	}
}
