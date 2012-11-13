package com.abbink.langpop.aggregate

import com.abbink.langpop.query.{ComponentRegistry => QueryComponentRegistry}
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorComponent
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorComponent
import com.abbink.langpop.aggregate.specific.github.GithubAggregator
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregator
import com.abbink.langpop.aggregate.specific.CombinedSpecificAggregatorFactoryComponent
import com.abbink.langpop.aggregate.specific.CombinedSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.github.GithubAggregatorFactory
import com.abbink.langpop.aggregate.specific.stackoverflow.StackoverflowAggregatorFactory

trait ComponentRegistry extends
	QueryDependencyComponent with
	AggregatorComponent with
	CombinedSpecificAggregatorFactoryComponent with
	GithubAggregatorComponent with
	StackoverflowAggregatorComponent
{
	def query : Query = QueryImpl
	def aggregator : Aggregator = AggregatorImpl
	def combinedSpecificAggregatorFactory : CombinedSpecificAggregatorFactory = CombinedSpecificAggregatorFactoryImpl
	def githubAggregatorFactory : GithubAggregatorFactory = GithubAggregatorFactoryImpl
	def stackoverflowAggregatorFactory : StackoverflowAggregatorFactory = StackoverflowAggregatorFactoryImpl
}

/**
  * langpop-query
  */
trait Query extends QueryComponentRegistry

/**
  * Gathering dependencies here
  */
trait QueryDependencyComponent {
	def query:Query
	
	object QueryImpl extends Query
}