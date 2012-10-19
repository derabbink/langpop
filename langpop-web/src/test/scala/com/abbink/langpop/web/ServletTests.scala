package com.abbink.langpop.web

import org.scalatest.FunSuite
import org.scalatra.test.scalatest._

class ServletTests extends ScalatraSuite with FunSuite {
	addServlet(classOf[LangpopServlet], "/langpop/*")
	
	test("simple get") {
		get("/langpop") {
			status should equal (200)
			body should include ("Hello")
		}
	}
}