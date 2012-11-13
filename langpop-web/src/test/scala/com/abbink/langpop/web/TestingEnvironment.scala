package com.abbink.langpop.web

import com.abbink.langpop.aggregate.AggregateTestImpl

trait TestingEnvironment extends
	AggregateDependencyComponent
{
	def aggregate : Aggregate = AggregateTestImpl //test implementation
}
