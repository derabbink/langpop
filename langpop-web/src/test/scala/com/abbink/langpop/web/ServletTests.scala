package com.abbink.langpop.web

import org.scalatest.FunSuite
import org.scalatra.test.scalatest._

class ServletTests extends ScalatraSuite with FunSuite {
	addServlet(classOf[LangpopServlet], "/langpop/*")
	
	test("get hello world (index)") {
		get("/langpop") {
			status should equal (200)
			body should include ("Hello")
		}
	}
	
	test("get date/language") {
		get("/langpop/2012-10-19/scala") {
			status should equal (200)
			body should include ("2012-10-19/scala");
		}
	}
}