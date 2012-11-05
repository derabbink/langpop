package com.abbink.langpop.aggregate.specific.github

import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import java.util.Date

object GithubAggregatorFactoryTestImpl extends GithubAggregatorFactory {
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
