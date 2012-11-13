package com.abbink.langpop.aggregate

import com.abbink.langpop.aggregate.specific.CombinedSpecificAggregatorFactoryComponent
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorComponent
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorFactoryTestImpl
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorFactoryTestImpl
import com.abbink.langpop.query.QueryTestImpl

trait TestingEnvironment extends
	QueryDependencyComponent with
	AggregatorComponent with
	CombinedSpecificAggregatorFactoryComponent with
	GithubAggregatorComponent with
	StackoverflowAggregatorComponent
{
	def query = QueryTestImpl //test implementation
	def aggregator = AggregatorTestImpl //test implementation
	def githubAggregatorFactory = GithubAggregatorFactoryTestImpl //test implementation
	def stackoverflowAggregatorFactory = StackoverflowAggregatorFactoryTestImpl //test implementation
	def combinedSpecificAggregatorFactory = CombinedSpecificAggregatorFactoryImpl //default, because individual specific aggregator factories are already test-specific
}