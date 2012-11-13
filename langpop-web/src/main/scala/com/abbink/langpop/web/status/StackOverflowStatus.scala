package com.abbink.langpop.web.status

import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.ComponentRegistry
import com.abbink.langpop.web.auth.StackOverflowAuth
import com.abbink.langpop.web.auth.StackOverflowAuthComponent

trait StackOverflowStatus {
	
	def isReady() : Boolean
	
}

trait StackOverflowStatusComponent extends
	StackOverflowAuthComponent
{
	def stackOverflowStatus:StackOverflowStatus
	
	object StackOverflowStatusImpl extends StackOverflowStatus {
		
		def isReady() : Boolean = {
			stackOverflowAuth.isAuthenticated()
		}
		
	}
}
