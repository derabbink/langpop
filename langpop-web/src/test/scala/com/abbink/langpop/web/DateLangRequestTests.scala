package com.abbink.langpop.web

import org.scalatest.FunSuite
import org.scalatra.test.scalatest._
import org.scalatest.BeforeAndAfter
import com.abbink.langpop.aggregate.Aggregator
import org.scalatest.BeforeAndAfterAll

class DateLangRequestTests extends ScalatraSuite with FunSuite with TestingEnvironment {
	addServlet(classOf[LangpopServlet], "/langpop/*")
	
//	override def beforeAll = {
//		super.beforeAll()
//		aggregator.start()
//	}
//	
//	override def afterAll = {
//		super.afterAll()
//		aggregator.system.shutdown()
//	}
	
	
	
	test("get date/language") {
		get("/langpop/1352649165/scala") {
			status should equal (200)
			body should include ("1352649165");
		}
	}
	
	test("get date/language 2") {
		get("/langpop/1352649165/scala/") {
			status should equal (200)
			body should include ("1352649165");
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
