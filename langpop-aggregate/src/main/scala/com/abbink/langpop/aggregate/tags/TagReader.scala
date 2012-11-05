package com.abbink.langpop.aggregate.tags

import java.io.File
import akka.actor.Actor
import scala.io.Source
import com.abbink.langpop.aggregate.Aggregator
import akka.event.Logging

object TagReader {
	sealed trait TagReaderMessage
	case class ReadFile(file : String) extends TagReaderMessage
}

class TagReader extends Actor with TagFileReader {
	import TagReader._
	
	val log = Logging(context.system, this)
	
	override def preStart() = {
		log.debug("Starting TagReader")
	}
	
	def receive = {
		case message : TagReaderMessage => message match {
			case ReadFile(f) =>
				var tags = read(f)
				sender ! tags
				context.stop(self)
		}
		case m => log.warning("Received unrecognized message: " + m)
	}
}

trait TagFileReader {
	
	def read(file:String) : Seq[String] = {
		Source.fromURL(
				getClass.getResource(file)).
			getLines.toList.filter(line => ""!=line)
	}
}

