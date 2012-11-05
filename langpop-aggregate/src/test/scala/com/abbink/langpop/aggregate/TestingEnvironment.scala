package com.abbink.langpop.aggregate

import com.abbink.langpop.aggregate.specific.CombinedSpecificAggregatorFactoryComponent
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorComponent
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorFactoryTestImpl
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorFactoryTestImpl

trait TestingEnvironment extends
	AggregatorComponent with
	CombinedSpecificAggregatorFactoryComponent with
	GithubAggregatorComponent with
	StackoverflowAggregatorComponent
{
	val aggregator = AggregatorImpl //default
	val combinedSpecificAggregatorFactory = CombinedSpecificAggregatorFactoryImpl //default
	val githubAggregatorFactory = GithubAggregatorFactoryTestImpl //test implementation
	val stackoverflowAggregatorFactory = StackoverflowAggregatorFactoryTestImpl
}