package com.abbink.langpop.aggregate.tags

import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.actor.ActorSystem
import org.scalatest.WordSpec
import akka.actor.Props
import com.abbink.langpop.aggregate.Aggregator
import com.typesafe.config.ConfigFactory

class TagReaderSuite(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpec with MustMatchers with BeforeAndAfterAll {
	
	import TagReader._
	import Aggregator._
	
	def this() = this(ActorSystem("TagReaderSuite", ConfigFactory.load()))
	
	override def afterAll = {
		system.shutdown()
	}
	
	"A TagReader actor" must {
		"send back seq of 3 tags" in {
			val tagsfile = config.getString("langpop.aggregate.tagsfile")
			val ref = system.actorOf(Props[TagReader])
			ref ! ReadFile(tagsfile)
			
			expectMsg(Seq("c#", "java", "scala"))
		}
	}
	
}
