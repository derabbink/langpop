package com.abbink.langpop.aggregate

import com.abbink.langpop.web.Aggregate

object AggregateTestImpl extends Aggregate {
	
	override def aggregator = AggregatorTestImpl
	
}

object AggregatorTestImpl extends Aggregator {
	
	def retrieve(tags:Set[String], timestamp:Long) : CombinedResponse = {
		val git = Map[String, Long]()
		val stack = Map[String, Long]()
		
		CombinedResponse(tags, timestamp, git, stack)
	}
	
}
