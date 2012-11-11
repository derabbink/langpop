package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}
import com.abbink.langpop.aggregate.AggregatorTestImpl
import com.abbink.langpop.aggregate.Aggregator

trait TestingEnvironment extends
	AggregatorComponentRegistry
{
	override def aggregator : Aggregator = AggregatorTestImpl //test implementation
}
