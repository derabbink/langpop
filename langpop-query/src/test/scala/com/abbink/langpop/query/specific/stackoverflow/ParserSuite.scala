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
	
	test("parsing valid /event JSON") {
		val input = """ {"total":5,"page_size":100,"page":1,"type":"event","items":[
				{"event_type":"comment_posted","event_id":18270399,"creation_date":1352897995},
				{"event_type":"user_created","event_id":1823757,"creation_date":1352897994},
				{"event_type":"post_edited","event_id":13369568,"creation_date":1352897993},
				{"event_type":"question_posted","event_id":13379299,"creation_date":1352897988},
				{"event_type":"answer_posted","event_id":13379298,"creation_date":1352897986}
			],"quota_remaining":9998,"quota_max":10000,"has_more":false} """
		//val expected = Some(JObject(List(JField("numbers",JArray(List(JInt(1), JInt(2), JInt(3), JInt(4)))))))
		val actual = Parser.parse(input)
		println(actual)
		//assert(expected == actual)
	}
}