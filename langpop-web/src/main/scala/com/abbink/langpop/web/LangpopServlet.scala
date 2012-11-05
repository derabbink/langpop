package com.abbink.langpop.web

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.control.Exception.catching
import java.text.ParseException
import org.scalatra.scalate.ScalateSupport
import org.scalatra.ScalatraServlet

class LangpopServlet extends ScalatraServlet with ScalateSupport with ComponentRegistry {
	
	get("/") {
		<html>
			<body>
				<h1>Hello, world!</h1>
				Say <a href="hello-scalate">hello to Scalate</a>.
			</body>
		</html>
	}
	
	get("/*/*") {
		val format:DateFormat = new SimpleDateFormat("yyyy-MM-dd")
		var date:Option[Date] = catching(classOf[ParseException]) opt { format.parse(multiParams("splat")(0)) }
		val lang:String = multiParams("splat")(1)
		
		var jdate:Date = date match {
			case Some(d) => d
			case None => halt(404)
		}
		
		if (lang isEmpty)
			halt(404)
		
		try {
			println("retrieving "+lang+", "+date)
			aggregator.retrieve(lang, jdate)
		}
		catch {
			case e:Exception =>
				var m : String = " "+ e.getMessage()
				var t : String = " "+ e.getStackTraceString
				println(m)
				println(t)
			case a => println("something else "+a)
		}
		<html>
			<body>
				<h1>{format format jdate}/{lang}</h1>
			</body>
		</html>
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
