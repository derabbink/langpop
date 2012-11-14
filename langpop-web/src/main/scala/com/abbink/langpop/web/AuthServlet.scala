package com.abbink.langpop.web

import org.scalatra.scalate.ScalateSupport
import org.scalatra.ScalatraServlet
import java.net.URLDecoder

class AuthServlet extends ScalatraServlet with ScalateSupport with ComponentRegistry {
	
	get("/") {
		<html>
			<body>
				<h1>Login status</h1>
				<ul>
					<li>StackOverflow: {stackoverflowLogin()}</li>
					<li>GitHub: {githubLogin}</li>
					<li>Issue query <a href="/langpop">here</a></li>
					<li>Check status <a href="/status">here</a></li>
				</ul>
			</body>
		</html>
	}
	
	private def stackoverflowLogin() = {
		if (stackOverflowAuth.isAuthenticated()) {
			<strong>signed in</strong>
			<a href="/auth/stackoverflow/logout">sign out</a>
		}
		else {
			<strong>signed out</strong>
			<a href="/auth/stackoverflow/login">sign in</a>
		}
	}
	
	private def githubLogin() = {
		<i>not implemented yet</i>
	}
	
	get("/stackoverflow/logout") {
		if (stackOverflowAuth.isAuthenticated())
			stackOverflowAuth.clearAuth()
		redirect("/auth")
	}
	
	get("/stackoverflow/login") {
		if (stackOverflowAuth.isAuthenticated())
			redirect("/auth")
		else
			redirect(stackOverflowAuth.buildOAuthUrl())
	}
	
	get("/stackoverflow/redirect") {
		val code = URLDecoder.decode(params("code"))
		//val state = params.get("state") map URLDecoder.decode
		
		stackOverflowAuth.finalizeAuth(code)
		redirect("/auth")
	}
}
