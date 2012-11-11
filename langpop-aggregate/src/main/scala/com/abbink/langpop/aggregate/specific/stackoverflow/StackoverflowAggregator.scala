package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import java.util.Date
import com.abbink.langpop.aggregate.specific.SingularSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl
import akka.event.Logging

trait StackoverflowAggregator extends SpecificAggregator {
	
}

trait StackoverflowAggregatorFactory extends SingularSpecificAggregatorFactory {
	override def create(tags:Seq[String], beginDate:Date) : SpecificAggregator
}

trait StackoverflowAggregatorComponent {
	
	val stackoverflowAggregatorFactory:StackoverflowAggregatorFactory
	
	object StackoverflowAggregatorFactoryImpl extends StackoverflowAggregatorFactory {
		override def create(tags:Seq[String], beginDate:Date) : StackoverflowAggregator = {
			println("  doing stackoverflow 2")
			new StackoverflowAggregatorImpl(tags, beginDate)
		}
	}
	
	class StackoverflowAggregatorImpl(tags:Seq[String], beginDate:Date) extends SpecificAggregatorImpl(tags, beginDate) with StackoverflowAggregator {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting StackoverflowAggregator")
		}
	}
}
