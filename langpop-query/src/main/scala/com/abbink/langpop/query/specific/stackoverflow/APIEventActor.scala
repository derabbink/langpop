package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.StackoverflowAPIActorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.StackoverflowAPIActorResponseMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Uri
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Json
import akka.actor.Actor
import java.net.URI
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import net.liftweb.json.JsonParser
import net.liftweb.json.JsonAST.JValue
import scala.util.control.Exception.catching
import net.liftweb.json.JsonParser.ParseException

object StackoverflowAPIActor {
	sealed trait StackoverflowAPIActorMessage
	case class Uri(uri:URI) extends StackoverflowAPIActorMessage
	
	sealed trait StackoverflowAPIActorResponseMessage
	case class Json(response:Option[AnyRef]) extends StackoverflowAPIActorResponseMessage
}

class StackoverflowAPIActor extends Actor {
	
	def receive = {
		case message : StackoverflowAPIActorMessage => message match {
			case Uri(uri) => sender ! Json(getAndParse(uri))
		}
	}
	
	private def getAndParse(uri: URI) : Option[AnyRef] = {
		val client : HttpClient = new DefaultHttpClient()
		val get = new HttpGet(uri)
		val response = client.execute(get)
		val statuscode = response.getStatusLine().getStatusCode()
		
		if (statuscode >= 200 && statuscode < 300) {
			val entity = response.getEntity()
			val jsonContent = EntityUtils.toString(entity)
			val parsed = Parser.parse(jsonContent)
			Some(parsed)
		}
		else
			None
	}
	
}


object Parser {
	
	/**
	  * wrapper method introduced to explore json-lift with tests
	  */
	def parse(json:String) : Option[JValue] = {
		catching(classOf[ParseException]) opt JsonParser.parse(json)
	}
}