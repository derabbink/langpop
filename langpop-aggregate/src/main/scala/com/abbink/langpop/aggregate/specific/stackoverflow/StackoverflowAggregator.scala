package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import com.abbink.langpop.aggregate.specific.SingularSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import akka.event.Logging

trait StackoverflowAggregator extends SpecificAggregator {
	
}

trait StackoverflowAggregatorFactory extends SingularSpecificAggregatorFactory {
	override def create(tags:Seq[String], beginTimestamp:Long) : SpecificAggregator
}

trait StackoverflowAggregatorComponent {
	
	def stackoverflowAggregatorFactory:StackoverflowAggregatorFactory
	
	object StackoverflowAggregatorFactoryImpl extends StackoverflowAggregatorFactory {
		override def create(tags:Seq[String], beginTimestamp:Long) : StackoverflowAggregator = {
			new StackoverflowAggregatorImpl(tags, beginTimestamp)
		}
	}
	
	class StackoverflowAggregatorImpl(tags:Seq[String], beginTimestamp:Long) extends SpecificAggregatorImpl(tags, beginTimestamp) with StackoverflowAggregator {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting StackoverflowAggregator")
		}
	}
}
