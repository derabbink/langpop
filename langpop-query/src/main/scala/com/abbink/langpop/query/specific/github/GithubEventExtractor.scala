package com.abbink.langpop.query.specific.github

import com.abbink.langpop.query.specific.SpecificEventExtractor
import com.abbink.langpop.query.specific.SingularSpecificEventExtractorFactory
import akka.event.Logging
import com.abbink.langpop.query.specific.SpecificEventExtractorImpl
import akka.actor.ActorRef
import akka.actor.ActorSystem

trait GithubEventExtractor extends SpecificEventExtractor {

}

trait GithubEventExtractorFactory extends SingularSpecificEventExtractorFactory {
	override def create(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : SpecificEventExtractor
}

trait GithubEventExtractorComponent {
	def githubEventExtractorFactory:GithubEventExtractorFactory
	
	object GithubEventExtractorFactoryImpl extends GithubEventExtractorFactory {
		override def create(system:ActorSystem, aggregator:ActorRef, beginTimestamp:Long) : GithubEventExtractor = {
			new GithubEventExtractorImpl(system, aggregator, beginTimestamp)
		}
	}
	
	class GithubEventExtractorImpl(override val system:ActorSystem, override val aggregator:ActorRef, val beginTimestamp:Long) extends SpecificEventExtractorImpl(system, aggregator) with GithubEventExtractor {
		
		private val log = Logging(context.system, this)
		
		protected def start(args:AnyRef*) {
			//TODO start consuming events
		}
		
		protected def stop() = {
			//TODO
		}
		
		protected def isRunning() : Boolean = {
			//TODO
			false
		}
		
		override def preStart() = {
			log.debug("Starting GithubEventExtractor")
		}
	}
}
