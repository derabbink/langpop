package com.abbink.langpop.aggregate

import java.util.Date

sealed trait Data
case class CombinedResponse(tags:Set[String], timestamp:Long, github:Map[String, Long], stackoverflow:Map[String, Long]) extends Data
