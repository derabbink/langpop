package com.abbink.langpop.aggregate.tags

import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.ActorSystem
import org.scalatest.WordSpec
import akka.actor.Props
import com.abbink.langpop.aggregate.Aggregator

class TagReaderSuite(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {
	
	import TagReader._
	import Aggregator._
	
	def this() = this(ActorSystem("TagReaderSuite"))
	
	override def afterAll = {
		system.shutdown()
	}
	
	"A TagReader actor " must {
		"send back seq of 3 tags" in {
			val ref = system.actorOf(Props[TagReader])
			ref ! ReadFile("/tags.txt")
			
			expectMsg(TagSeq(Seq("c#", "java", "scala")))
		}
	}
	
}
