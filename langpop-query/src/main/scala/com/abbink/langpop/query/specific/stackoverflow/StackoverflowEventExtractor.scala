package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl

import akka.actor.ActorRef
import akka.event.Logging

trait StackoverflowEventExtractor extends SpecificEventExtractor {

}

trait StackoverflowEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait StackoverflowEventExtractorComponent {
	def stackoverflowEventExtractorFactory:StackoverflowEventExtractorFactory
	
	object StackoverflowEventExtractorFactoryImpl extends StackoverflowEventExtractorFactory {
		override def create(aggregator:ActorRef, beginTimestamp:Long) : StackoverflowEventExtractor = {
			new StackoverflowEventExtractorImpl(aggregator, beginTimestamp)
		}
	}
	
	/**
	  * this actor 
	  */
	class StackoverflowEventExtractorImpl(override val aggregator:ActorRef, val beginTimestamp:Long) extends SpecificEventExtractorImpl(aggregator) with StackoverflowEventExtractor {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting StackoverflowEventExtractor")
		}
	}
}
