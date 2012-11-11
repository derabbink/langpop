package com.abbink.langpop.web

import com.abbink.langpop.aggregate.{ComponentRegistry => AggregatorComponentRegistry}
import com.abbink.langpop.web.auth.StackOverflowAuthComponent
import com.abbink.langpop.web.auth.StackOverflowAuth

trait ComponentRegistry extends
	AggregatorComponentRegistry with
	StackOverflowAuthComponent
{
	def stackOverflowAuth : StackOverflowAuth = StackOverflowAuthImpl
}
