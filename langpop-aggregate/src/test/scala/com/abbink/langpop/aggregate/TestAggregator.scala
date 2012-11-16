package com.abbink.langpop.aggregate

object AggregatorTestImpl extends Aggregator {
	def init() = {}
	
	def retrieve(tags:Set[String], timestamp:Long) : CombinedResponse = {
		val git = Map[String, Long]()
		val stack = Map[String, Long]()
		
		CombinedResponse(tags, timestamp, git, stack)
	}
	
}