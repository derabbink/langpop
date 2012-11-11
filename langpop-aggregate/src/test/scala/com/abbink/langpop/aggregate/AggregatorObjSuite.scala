package com.abbink.langpop.aggregate

import java.text.SimpleDateFormat
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import com.typesafe.config.ConfigFactory

class AggregatorObjSuite extends FunSuite with TestingEnvironment {
	
	val config = ConfigFactory.load()
	val starttime = config.getLong("test.langpop.aggregate.starttime")
	
	test("Retrieving empty result for tag 'foo'") {
		val tags = Set("foo")
		val expected = CombinedResponse(tags, starttime, Map(), Map())
		
		val actual = aggregator.retrieve(tags, starttime)
		assert(actual === expected)
	}
}
