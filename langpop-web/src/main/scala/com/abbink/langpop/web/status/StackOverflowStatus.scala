package com.abbink.langpop.web.status

import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.ComponentRegistry
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.auth.StackOverflowAuthComponent
import com.abbink.langpop.web.QueryDependencyComponent

trait StackOverflowStatus {
	
	def isReady() : Boolean
	
	def isRunning() : Boolean
	
	def requestCount() : Int
}

trait StackOverflowStatusComponent extends
	StackOverflowAuthComponent with
	QueryDependencyComponent
{
	
	def stackOverflowStatus:StackOverflowStatus
	
	object StackOverflowStatusImpl extends StackOverflowStatus {
		
		def isReady() : Boolean = {
			stackOverflowAuth.isAuthenticated()
		}
		
		def isRunning() : Boolean = {
			query.querySystem.isRunningStackOverflow()
		}
		
		def requestCount() : Int = {
			query.querySystem.requestCountStackOverflow()
		}
	}
}
