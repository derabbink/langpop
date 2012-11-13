package com.abbink.langpop.query.specific.stackoverflow

import org.scalatest.FunSuite
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JInt

class ParserSuite extends FunSuite {
	
	test("Parsing valid JSON") {
		val input = """ { "numbers" : [1, 2, 3, 4] } """
		val expected = Some(JObject(List(JField("numbers",JArray(List(JInt(1), JInt(2), JInt(3), JInt(4)))))))
		val actual = Parser.parse(input)
		//println(actual)
		assert(expected == actual)
	}
	
	test("Parsing invalid JSON") {
		val input = """ { "numbers : [1, 2, 3, ] } """
		val expected = None
		val actual = Parser.parse(input)
		assert(expected == actual)
	}
	
}