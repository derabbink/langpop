package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import java.util.Date

object StackoverflowAggregatorFactoryTestImpl extends StackoverflowAggregatorFactory {
	override def create(tags:Seq[String], beginDate:Date) : GithubAggregator = {
		new GithubAggregatorTestImpl(tags, beginDate)
	}
}

class GithubAggregatorTestImpl(tags:Seq[String], beginDate:Date) extends SpecificAggregatorImpl(tags, beginDate) with GithubAggregator {
	
	override def preStart() = {
		log.debug("Starting GithubAggregatorTestImpl")
	}
	
	protected override def startActors() = {
		//leave empty. we don't want to start scraping data in tests
	}
}
