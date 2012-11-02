package com.abbink.langpop.aggregate.specific.github

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import akka.event.Logging

class GithubAggregator extends SpecificAggregator {
	
	override def preStart() = {
		log.debug("Starting GithubAggregator")
	}
	
	override def startActors() = {
		
	}
}
