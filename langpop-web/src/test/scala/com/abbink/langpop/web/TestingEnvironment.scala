package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}

trait TestingEnvironment extends
	AggregatorComponentRegistry
{
	//TODO replace AggregatorComponentRegistry entries with *locally* mocked versions of aggregator implementations
}