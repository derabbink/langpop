package com.abbink.langpop.web

import org.scalatest.FunSuite
import org.scalatra.test.scalatest._

class DateLangRequestTests extends ScalatraSuite with FunSuite {
	addServlet(classOf[LangpopServlet], "/langpop/*")
	
	test("get date/language") {
		get("/langpop/2012-10-19/scala") {
			status should equal (200)
			body should include ("2012-10-19/scala");
		}
	}
	
	test("get date/language 2") {
		get("/langpop/2012-10-19/scala/") {
			status should equal (200)
			body should include ("2012-10-19/scala/");
		}
	}
	
	test("get date (no language)") {
		get("/langpop/2012-10-19") {
			status should equal (404)
		}
	}
	
	test("get date (no language) 2") {
		get("/langpop/2012-10-19/") {
			status should equal (404)
		}
	}
	
	test("get langage (no date)") {
		get("/langpop//scala") {
			status should equal (404)
		}
	}
	
	test("get langage (no date) 2") {
		get("/langpop//scala/") {
			status should equal (404)
		}
	}
}
