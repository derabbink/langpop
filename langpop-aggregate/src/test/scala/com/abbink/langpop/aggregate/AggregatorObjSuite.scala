package com.abbink.langpop.aggregate

import java.text.DateFormat
import java.text.SimpleDateFormat

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite

import com.typesafe.config.ConfigFactory

class AggregatorObjSuite extends FunSuite with BeforeAndAfterAll with TestingEnvironment {
	
	val config = ConfigFactory.load()
	val format:DateFormat = new SimpleDateFormat("yyyy-MM-dd")
	val startDate = format parse (config getString "langpop.aggregate.startdate") 
	
	override def beforeAll = {
		aggregator.start()
	}
	
	override def afterAll = {
		aggregator.system.shutdown()
	}
	
	test("Retrieving empty result for tag 'foo'") {
		val tag = "foo"
		val expected = CombinedResponse(tag, startDate, 0, 0)
		
		val actual = aggregator.retrieve(tag, startDate)
		assert(actual === expected)
	}
}
