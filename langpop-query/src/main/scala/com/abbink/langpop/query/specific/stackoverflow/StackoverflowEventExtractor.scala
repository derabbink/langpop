package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.SpecificEventExtractor
import akka.event.Logging
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl

trait StackoverflowEventExtractor extends SpecificEventExtractor {

}

trait StackoverflowEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(beginTimestamp:Long) : SpecificEventExtractor
}

trait StackoverflowEventExtractorComponent {
	def stackoverflowEventExtractorFactory:StackoverflowEventExtractorFactory
	
	object StackoverflowEventExtractorFactoryImpl extends StackoverflowEventExtractorFactory {
		override def create(beginTimestamp:Long) : StackoverflowEventExtractor = {
			new StackoverflowEventExtractorImpl(beginTimestamp)
		}
	}
	
	class StackoverflowEventExtractorImpl(val beginTimestamp:Long) extends SpecificEventExtractorImpl with StackoverflowEventExtractor {
		
		private val log = Logging(context.system, this)
		
		override def preStart() = {
			log.debug("Starting StackoverflowEventExtractor")
		}
	}
}
