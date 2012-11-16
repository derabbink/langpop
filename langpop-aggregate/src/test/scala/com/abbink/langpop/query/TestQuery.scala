package com.abbink.langpop.query

import com.abbink.langpop.aggregate.Query
import akka.actor.ActorRef
import akka.actor.ActorSystem

object QueryTestImpl extends Query {
	
	override def querySystem = QuerySystemTestImpl
	
}

object QuerySystemTestImpl extends QuerySystem {
	def init(actorSystem:ActorSystem, githubActorRef:ActorRef, stackoverflowActorRef:ActorRef, startTimestamp:Long) {
		//TODO
	}
	
	def startStackOverflow(accessToken:String, apiKey:String) = {
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
	
	def isRunningStackOverflow() : Boolean = {
		//TODO
		true
	}
	
	def isRunningGithub() : Boolean = {
		//TODO
		true
	}
	
	def requestCountStackOverflow() : Int = {
		//TODO
		0
	}
	
	def requestCountGithub() : Int = {
		//TODO
		0
	}
}