package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregateComponentRegistry}
import com.abbink.langpop.aggregate.QueryDependencyComponent
import com.abbink.langpop.aggregate.Query
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
	def query : Query = aggregate.query
	def stackOverflowAuth : StackOverflowAuth = StackOverflowAuthImpl
	def stackOverflowStatus : StackOverflowStatus = StackOverflowStatusImpl
}

/**
  * langpop-aggregate
  */
trait Aggregate extends AggregateComponentRegistry

/**
  * linking dependency here
  */
trait AggregateDependencyComponent {
	def aggregate:Aggregate
	
	object AggregateImpl extends Aggregate
}
