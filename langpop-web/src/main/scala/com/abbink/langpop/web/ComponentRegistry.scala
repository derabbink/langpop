package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}
import com.abbink.langpop.web.auth.StackOverflowAuthComponent
import com.abbink.langpop.web.auth.StackOverflowAuth

trait ComponentRegistry extends
	DependencyComponent with
	StackOverflowAuthComponent
{
	def aggregate : Aggregate = AggregateImpl
	def stackOverflowAuth : StackOverflowAuth = StackOverflowAuthImpl
}

/**
 * langpop-aggregate
 */
trait Aggregate extends AggregatorComponentRegistry

/**
 * Gathering dependencies here
 */
trait DependencyComponent {
	def aggregate:Aggregate
	
	object AggregateImpl extends Aggregate
}
