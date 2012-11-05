package com.abbink.langpop.aggregate.tags

import org.scalatest.FunSuite
import java.io.File
import scala.io.Source
import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory

class TagFileReaderSuite extends FunSuite {
	
	val config = ConfigFactory.load()
	
	test("Loading tags from file") {
		val expectedTags : Seq[String] = Seq("c#", "java", "scala")
		val tagsfile = config.getString("langpop.aggregate.tagsfile")
		
		class TestTagReader extends TagFileReader
		val ttr = new TestTagReader
		var actualTags = ttr read tagsfile
		
		assert(actualTags == expectedTags)
	}
	
}