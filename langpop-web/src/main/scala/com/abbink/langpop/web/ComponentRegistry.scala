package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregateComponentRegistry}
import com.abbink.langpop.web.auth.StackOverflowAuthComponent
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.status.StackOverflowStatusComponent
import com.abbink.langpop.web.status.StackOverflowStatus

trait ComponentRegistry extends
	AggregateDependencyComponent with
	StackOverflowAuthComponent with
	StackOverflowStatusComponent
{
	def aggregate : Aggregate = AggregateImpl
	def stackOverflowAuth : StackOverflowAuth = StackOverflowAuthImpl
	def stackOverflowStatus : StackOverflowStatus = StackOverflowStatusImpl
}

/**
  * langpop-aggregate
  */
trait Aggregate extends AggregateComponentRegistry

/**
  * Gathering dependencies here
  */
trait AggregateDependencyComponent {
	def aggregate:Aggregate
	
	object AggregateImpl extends Aggregate
}
