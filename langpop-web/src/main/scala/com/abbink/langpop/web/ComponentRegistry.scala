package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregateComponentRegistry}
import com.abbink.langpop.query.{ComponentRegistry => QueryComponentRegistry}
import com.abbink.langpop.web.auth.StackOverflowAuthComponent
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.status.StackOverflowStatusComponent
import com.abbink.langpop.web.status.StackOverflowStatus

trait ComponentRegistry extends
	AggregateDependencyComponent with
	QueryDependencyComponent with
	StackOverflowAuthComponent with
	StackOverflowStatusComponent
{
	def aggregate : Aggregate = AggregateImpl
	def query : Query = QueryImpl
	def stackOverflowAuth : StackOverflowAuth = StackOverflowAuthImpl
	def stackOverflowStatus : StackOverflowStatus = StackOverflowStatusImpl
}

/**
  * langpop-aggregate
  */
trait Aggregate extends AggregateComponentRegistry

/**
  * langpop-query
  */
trait Query extends QueryComponentRegistry

/**
  * linking dependency here
  */
trait AggregateDependencyComponent {
	def aggregate:Aggregate
	
	object AggregateImpl extends Aggregate
}

/**
  * linking dependency here
  */
trait QueryDependencyComponent {
	def query:Query
	
	object QueryImpl extends Query
}
