package com.abbink.langpop.aggregate

import java.util.Date

sealed trait Data
case class Request(tag:String, date:Date) extends Data
case class CombinedResponse(tag:String, date:Date, github:Long, stackoverflow:Long) extends Data