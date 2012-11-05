package com.abbink.langpop.aggregate.specific.stackoverflow

import com.abbink.langpop.aggregate.specific.SpecificAggregator
import java.util.Date
import com.abbink.langpop.aggregate.specific.SingularSpecificAggregatorFactory
import com.abbink.langpop.aggregate.specific.SpecificAggregatorImpl

trait StackoverflowAggregator extends SpecificAggregator {
	
}

trait StackoverflowAggregatorFactory extends SingularSpecificAggregatorFactory {
	override def create(tags:Seq[String], beginDate:Date) : StackoverflowAggregator
}

trait StackoverflowAggregatorComponent {
	
	val stackoverflowAggregatorFactory:StackoverflowAggregatorFactory
	
	object StackoverflowAggregatorFactoryImpl extends StackoverflowAggregatorFactory {
		override def create(tags:Seq[String], beginDate:Date) : StackoverflowAggregator = {
			new StackoverflowAggregatorImpl(tags, beginDate)
		}
	}
	
	class StackoverflowAggregatorImpl(tags:Seq[String], beginDate:Date) extends SpecificAggregatorImpl(tags, beginDate) with StackoverflowAggregator {
		
		override def preStart() = {
			log.debug("Starting StackoverflowAggregator")
		}
		
		protected override def startActors() = {
			
		}
	}
}
