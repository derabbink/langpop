package com.abbink.langpop.aggregate

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import com.typesafe.config.ConfigFactory
import java.util.Date

class AggregatorObjSuite extends FunSuite with TestingEnvironment {
	
	val config = ConfigFactory.load()
	val startDate = new Date(1000 * config.getLong("test.langpop.aggregate.starttime"))
	
	test("Retrieving empty result for tag 'foo'") {
		val tag = "foo"
		val expected = CombinedResponse(tag, startDate, 0, 0)
		
		val actual = aggregator.retrieve(tag, startDate)
		assert(actual === expected)
	}
}
