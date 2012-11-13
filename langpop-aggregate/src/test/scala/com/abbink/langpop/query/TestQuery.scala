package com.abbink.langpop.query

import com.abbink.langpop.aggregate.Query

object QueryTestImpl extends Query {
	
	override def querySystem = QuerySystemTestImpl
	
}

object QuerySystemTestImpl extends QuerySystem {
	def startStackOverflow() = {
		//TODO
	}
	
	def startGithub() = {
		//TODO
	}
	
	def stopStackOverflow() = {
		//TODO
	}
	
	def stopGithub() = {
		//TODO
	}
}