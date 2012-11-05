package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import java.util.Date

class StackoverflowAggregator(tags:Seq[String], beginDate:Date) extends SpecificAggregator(tags, beginDate) {
	
	override def preStart() = {
		log.debug("Starting StackoverflowAggregator")
	}
	
	override def startActors() = {
		
	}
}
