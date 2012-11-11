package com.abbink.langpop.web

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.control.Exception.catching
import java.text.ParseException
import org.scalatra.scalate.ScalateSupport
import org.scalatra.ScalatraServlet
import com.abbink.langpop.aggregate.CombinedResponse

class LangpopServlet extends ScalatraServlet with ScalateSupport with ComponentRegistry {
	
	get("/") {
		<html>
			<body>
				<h1>langpop</h1>
				<ul>
					<li>Issue query: e.g. <a href="/langpop/1352645381/scala">/langpop/1352645381/scala</a></li>
					<li>check data source login status <a href="/auth">here</a></li>
				</ul>
			</body>
		</html>
	}
	
	get("/*/*") {
		val splat = multiParams("splat")
		if (splat.size < 2)
			halt(404)
		
		val timestamp:Option[Long] = catching(classOf[NumberFormatException]) opt { splat(0).toLong }
		val langs:Set[String] = splat.tail.toSet
		
		if (timestamp == None || langs.size == 0)
			halt(404)
		
		val actualTimestamp:Long = timestamp.get
		contentType = "application/json"
		toJson(aggregator.retrieve(langs, actualTimestamp))
	}
	
	private def toJson(data:CombinedResponse) : String = {
		val sb:StringBuilder = new StringBuilder()
		sb append "{"
		
		sb append "timestamp:"
		sb append data.timestamp
		sb append ","
		
		sb append "github:{"
		sb append (popularityList(data.github))
		sb append "},"
		
		sb append "stackoverflow:{"
		sb append (popularityList(data.stackoverflow))
		sb append "}"
		
		sb append "}"
		sb.toString()
	}
	
	private def popularityList(popularities: Map[String, Long]) : String = {
		val sb:StringBuilder = new StringBuilder()
		//TODO write as fold
		var comma = false
		for ((tag, number) <- popularities) {
			if (comma)
				sb append ","
			else
				comma = true
			sb append """"""" //literal string containing "
			sb append tag
			sb append """":"""" // ":"
			sb append number
			sb append """""""
		}
		sb.toString()
	}
	
	notFound {
		// remove content type in case it was set through an action
		contentType = null
		// Try to render a ScalateTemplate if no route matched
		findTemplate(requestPath) map { path =>
			contentType = "text/html"
			layoutTemplate(path)
		} orElse serveStaticResource() getOrElse resourceNotFound()
	}
}
