package com.abbink.langpop.aggregate
import java.util.Date

object AggregatorTestImpl extends Aggregator {
	
	def retrieve(tag: String, date: Date) : CombinedResponse = {
		val git = 0
		val stack = 0
		
		CombinedResponse(tag, date, git, stack)
	}
	
}