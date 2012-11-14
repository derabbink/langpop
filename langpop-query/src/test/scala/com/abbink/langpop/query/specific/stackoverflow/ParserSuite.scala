package com.abbink.langpop.query.specific.stackoverflow

import org.scalatest.FunSuite
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JInt
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JBool

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
		val expected =
			Some(JObject(List(
				JField("total",JInt(5)),
				JField("page_size",JInt(100)),
				JField("page",JInt(1)),
				JField("type",JString("event")),
				JField("items",JArray(List(
					JObject(List(
						JField("event_type",JString("comment_posted")),
						JField("event_id",JInt(18270399)),
						JField("creation_date",JInt(1352897995))
					)),
					JObject(List(
						JField("event_type",JString("user_created")),
						JField("event_id",JInt(1823757)),
						JField("creation_date",JInt(1352897994))
					)),
					JObject(List(
						JField("event_type",JString("post_edited")),
						JField("event_id",JInt(13369568)),
						JField("creation_date",JInt(1352897993))
					)),
					JObject(List(
						JField("event_type",JString("question_posted")),
						JField("event_id",JInt(13379299)),
						JField("creation_date",JInt(1352897988))
					)),
					JObject(List(
						JField("event_type",JString("answer_posted")),
						JField("event_id",JInt(13379298)),
						JField("creation_date",JInt(1352897986))
					))
				))),
				JField("quota_remaining",JInt(9998)),
				JField("quota_max",JInt(10000)),
				JField("has_more",JBool(false))
			)))
		val actual = Parser.parse(input)
		//println(actual)
		assert(expected == actual)
	}
}