package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}
import com.abbink.langpop.web.auth.StackOverflowAuthComponent

trait ComponentRegistry extends
	AggregatorComponentRegistry with
	StackOverflowAuthComponent
{
	val stackOverflowAuth = StackOverflowAuthImpl
}
