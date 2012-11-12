package com.abbink.langpop.web

import com.abbink.langpop.aggregate.Aggregator
import com.abbink.langpop.aggregate.AggregatorTestImpl
import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}

trait TestingEnvironment extends
	DependencyComponent
{
	def aggregate : Aggregate = AggregateTestImpl //test implementation
}

object AggregateTestImpl extends Aggregate {
	override def aggregator : Aggregator = AggregatorTestImpl //test implementation
}
