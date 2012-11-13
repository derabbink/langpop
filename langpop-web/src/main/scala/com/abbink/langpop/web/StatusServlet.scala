package com.abbink.langpop.web

import org.scalatra.scalate.ScalateSupport
import org.scalatra.ScalatraServlet

class StatusServlet extends ScalatraServlet with ScalateSupport with ComponentRegistry {
	
	get("/") {
		<html>
			<body>
				<h1>System status</h1>
				<ul>
					<li>StackOverflow status: {stackoverflowStatus()}</li>
					<li>GitHub status: {githubStatus()}</li>
					<li>Actions: {actions()}</li>
					<li>Issue query <a href="/langpop">here</a></li>
				</ul>
			</body>
		</html>
	}
	
	private def stackoverflowStatus() = {
		if (stackOverflowStatus.isReady()) {
			//if ()
			"foo"
		}
		else {
			<strong>not ready</strong>
		}
	}
	
	private def githubStatus() = {
		<i>not implemented</i>
	}
	
	private def actions() = {
		<strong>some actions here</strong>
	}
}
