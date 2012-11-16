package com.abbink.langpop.query.specific.stackoverflow

import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.StackoverflowAPIActorMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.StackoverflowAPIActorResponseMessage
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Uri
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.UriParse
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Json
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowAPIActor.Extracted
import com.abbink.langpop.query.specific.stackoverflow.Parser.JsonExtract
import com.abbink.langpop.query.specific.stackoverflow.Parser.EventsWrapper
import com.abbink.langpop.query.specific.stackoverflow.Parser.Event
import com.abbink.langpop.query.specific.stackoverflow.Parser.classofEventsWrapper
import akka.actor.Actor
import java.net.URI
import org.apache.http.Header
import org.apache.http.protocol.HttpContext
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import net.liftweb.json.JsonParser
import net.liftweb.json.JsonAST.JValue
import scala.util.control.Exception.catching
import net.liftweb.json.JsonParser.ParseException
import net.liftweb.json.MappingException
import net.liftweb.json.DefaultFormats
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import java.io.IOException
import org.apache.http.HttpException
import org.apache.http.HttpResponseInterceptor
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import com.sun.jndi.dns.Header
import org.apache.http.client.entity.GzipDecompressingEntity

object StackoverflowAPIActor {
	sealed trait StackoverflowAPIActorMessage
	case class Uri(uri:URI) extends StackoverflowAPIActorMessage
	case class UriParse(datatype:Class[_], uri:URI) extends StackoverflowAPIActorMessage
	
	sealed trait StackoverflowAPIActorResponseMessage
	case class Json(data:Option[JValue]) extends StackoverflowAPIActorResponseMessage
	case class Extracted(datatype:Class[_], data:Option[JsonExtract]) extends StackoverflowAPIActorResponseMessage
}

class StackoverflowAPIActor extends Actor {
	
	def receive = {
		case message : StackoverflowAPIActorMessage => message match {
			case Uri(uri) => sender ! Json(getAndParse(uri))
			case UriParse(datatype, uri) =>
				println("getting, parsing and extracting")
				sender ! Extracted(datatype, getAndParseAndExtract(datatype, uri))
		}
	}
	
	private def getAndParse(uri: URI) : Option[JValue] = {
		println("getting and parsing")
		val client : DefaultHttpClient = new DefaultHttpClient()
		client.addRequestInterceptor(new GZipRequestInterceptor())
		client.addResponseInterceptor(new GZipResponseInterceptor())
		val get = new HttpGet(uri)
		val response = client.execute(get)
		val statuscode = response.getStatusLine().getStatusCode()
		get.releaseConnection()
		
		if (statuscode >= 200 && statuscode < 300) {
			val entity = response.getEntity()
			val jsonContent = EntityUtils.toString(entity)
			println("json content: "+jsonContent)
			Parser.parse(jsonContent)
		}
		else
			None
	}
	
	private def getAndParseAndExtract(datatype:Class[_], uri:URI) : Option[JsonExtract] = {
		val parsed = getAndParse(uri)
		datatype match {
			case classofEventsWrapper => Parser.extractEvents(parsed)
		}
	}
}

object Parser {
	
	/**
	  * wrapper method introduced to explore json-lift with tests
	  */
	def parse(json:String) : Option[JValue] = {
		catching(classOf[ParseException]) opt JsonParser.parse(json)
		//Some(JsonParser.parse(json))
	}
	
	//Cannot write generic function without complicated Manifest magic
	/**
	  * parses and extracts real objects from parsed json for events
	  */
	def extractEvents(parsed:Option[JValue]) : Option[EventsWrapper] = {
		parsed match {
			case None => None
			case Some(x) =>
				implicit val formats = DefaultFormats
				catching(classOf[MappingException]) opt x.extract[EventsWrapper]
				//Some(x.extract[EventsWrapper])
		}
	}
	
	//case classes for improving AST utility
	
	//events
	sealed trait JsonExtract
	case class EventsWrapper(backoff:Option[Int], total:Int, page_size:Int, page:Int, `type`:String, items:List[Event], quota_remaining:Int, quota_max:Int, has_more:Boolean) extends JsonExtract
	case class Event(event_type:String, event_id:Long, creation_date:Long)
	
	//cannot do pattern matching over generics. so we do it over arguments. This is ugly, and not quite as typesafe
	val classofEventsWrapper:Class[_] = classOf[EventsWrapper]
}

class GZipRequestInterceptor extends HttpRequestInterceptor {
	
	@throws(classOf[HttpException])
	@throws(classOf[IOException])
	def process(request:HttpRequest, context:HttpContext) = {
		if (!request.containsHeader("Accept-Encoding")) {
			request.addHeader("Accept-Encoding", "gzip")
		}
	}
}

class GZipResponseInterceptor extends HttpResponseInterceptor {
	
	@throws(classOf[HttpException])
	@throws(classOf[IOException])
	def process(response:HttpResponse, context:HttpContext) = {
		val entity : HttpEntity = response.getEntity()
		if (entity != null) {
			val ceheader:org.apache.http.Header = entity.getContentEncoding();
			if (ceheader != null) {
				var codecs = ceheader.getElements()
				for(codec <- codecs) {
					if (codec.getName().equalsIgnoreCase("gzip"))
						response.setEntity(new GzipDecompressingEntity(response.getEntity()))
				}
			}
		}
	}
	
}