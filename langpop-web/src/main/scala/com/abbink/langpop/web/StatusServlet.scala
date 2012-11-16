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
					<li>Check data source login status <a href="/auth">here</a></li>
					<li>Issue query <a href="/langpop">here</a></li>
				</ul>
			</body>
		</html>
	}
	
	private def stackoverflowStatus() = {
		if (stackOverflowStatus.isReady()) {
			if (stackOverflowStatus.isRunning()) {
				<strong>running</strong> <i>{stackOverflowStatus.requestCount()} requests made</i>
			}
			else {
				<i>not running</i>
			}
		}
		else {
			<strong>not ready</strong>
		}
	}
	
	private def githubStatus() = {
		<i>not implemented yet</i>
	}
	
	private def actions() = {
		if (stackOverflowStatus.isReady() /*and github is ready*/) {
			if (stackOverflowStatus.isRunning() /*or github is running*/) {
				<a href="/status/stop">stop all</a>
			}
			else {
				<a href="/status/start">start all</a>
			}
		}
		else {
			<i>fix authentication first</i>
		}
	}
	
	get("/start") {
		if (!stackOverflowStatus.isRunning()) {
			val token = stackOverflowAuth.token()
			if (token != None)
				query.querySystem.startStackOverflow(token.get, stackOverflowAuth.appKey())
		}
		//TODO github
		redirect("/status")
	}
	
	get("/stop") {
		if (stackOverflowStatus.isRunning()) {
			query.querySystem.stopStackOverflow()
		}
		//TODO github
		redirect("/status")
	}
}
