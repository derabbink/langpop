package com.abbink.langpop.web

import org.scalatra.test.specs2._

class AuthServletSpec extends ScalatraSpec { def is =
	"GET /auth on AuthServlet"			^
	"should return status 200"			! root200^
										end
	
	addServlet(classOf[AuthServlet], "/auth/*")
	
	def root200 = get("/auth") {
		status must_== 200
	}
}
