package com.abbink.langpop.web

import org.scalatest.FunSuite
import org.scalatra.test.scalatest._

class ServletTests extends ScalatraSuite with FunSuite {
	addServlet(classOf[LangpopServlet], "/langpop/*")
	addServlet(classOf[AuthServlet], "/auth/*")
	
	test("get langpop (index)") {
		get("/langpop") {
			status should equal (200)
			body should include ("langpop")
		}
	}
	
	test("get auth (index)") {
		get("/auth") {
			status should equal (200)
			body should include ("Login status")
		}
	}
}
