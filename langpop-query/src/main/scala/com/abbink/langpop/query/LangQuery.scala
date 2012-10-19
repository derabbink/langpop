package com.abbink.langpop.query

import java.util.Date

case class LangQuery(date:Date, lang:String) {
	
}

case class LangQueryResponse(date:Date, lang:String, value:Long) {
	
}