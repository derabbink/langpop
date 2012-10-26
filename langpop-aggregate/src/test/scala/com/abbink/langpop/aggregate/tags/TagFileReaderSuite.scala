package com.abbink.langpop.aggregate.tags

import org.scalatest.FunSuite
import java.io.File
import scala.io.Source
import akka.actor.ActorRef

class TagFileReaderSuite extends FunSuite {
	
	test("Loading tags from file") {
		val expectedTags : Seq[String] = Seq("c#", "java", "scala")
		
		class TestTagReader extends TagFileReader
		val ttr = new TestTagReader
		var actualTags = ttr read "/tags.txt"
		
		assert(actualTags == expectedTags)
	}
	
}