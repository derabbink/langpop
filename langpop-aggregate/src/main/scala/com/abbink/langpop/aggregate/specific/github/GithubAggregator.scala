package com.abbink.langpop.aggregate.specific.github

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import akka.event.Logging
import java.util.Date

class GithubAggregator(tags:Seq[String], beginDate:Date) extends SpecificAggregator(tags, beginDate) {
	
	override def preStart() = {
		log.debug("Starting GithubAggregator")
	}
	
	override def startActors() = {
		
	}
}
