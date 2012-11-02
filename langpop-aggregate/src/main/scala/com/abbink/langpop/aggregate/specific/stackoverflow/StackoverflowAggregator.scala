package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregator

class StackoverflowAggregator extends SpecificAggregator {
	
	override def preStart() = {
		log.debug("Starting StackoverflowAggregator")
	}
	
	override def startActors() = {
		
	}
}
