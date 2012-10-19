package com.abbink.langpop.query

import akka.actor.Actor

trait LangAggregator extends Actor {
	
	//TODO: create an ApiGateway and use it to throttle requests
	
	def receive = {
		case LangQueryResponse => //store popularity data
		case _ =>
	}
	
}
