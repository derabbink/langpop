package com.abbink.langpop.aggregate.tags

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import com.abbink.langpop.aggregate.Aggregator
import com.typesafe.config.ConfigFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import com.abbink.langpop.aggregate.CombinedResponse
import com.abbink.langpop.aggregate.Data

class AggregatorObjSuite extends FunSuite with BeforeAndAfter {
	
	val config = ConfigFactory.load()
	val format:DateFormat = new SimpleDateFormat("yyyy-MM-dd")
	val startDate = format parse (config getString "langpop.aggregate.startdate") 
	
	before {
		Aggregator.start()
	}
	
	test("Retrieving empty result for tag 'foo'") {
		val tag = "foo"
		val expected = CombinedResponse(tag, startDate, 0, 0)
		
		val actual = Aggregator.retrieve(tag, startDate)
		assert(actual === expected)
	}
}