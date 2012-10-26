package com.abbink.langpop.aggregate.tags

import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.ActorSystem
import org.scalatest.WordSpec
import com.abbink.langpop.aggregate.Aggregator
import akka.actor.Props
import java.util.Date

class AggregatorSuite(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {
	
	import Aggregator._
	
	def this() = this(ActorSystem("AggregatorSuite"))
	
	override def afterAll = {
		system.shutdown()
	}
	
	"An Aggregator actor" must {
		"start a TagReader actor" in {
			val ref = system.actorOf(Props[Aggregator])
			ref ! StartProcessing()
			
			//TODO test if actor is started
		}
	}
	
	"An Aggregator actor" must {
		"produce combined result of None for any tag and any date" in {
			val tag = "foo"
			val date = new Date()
			val ref = system.actorOf(Props[Aggregator])
			ref ! Query(tag, date)
			
			//TODO test if actor is started
			expectMsg(CombinedResult(tag, date, None, None))
		}
	}
}
