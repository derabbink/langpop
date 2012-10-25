package com.abbink.langpop.aggregate.tags

import java.io.File
import akka.actor.Actor
import scala.io.Source

object TagReader {
	sealed trait TagReaderMessage
	case class ReadFile(file : String) extends TagReaderMessage
}

class TagReader extends Actor with TagFileReader {
	import TagReader._
	
	def receive = {
		case message : TagReaderMessage => message match {
			case ReadFile(f) => read(f)
		}
	}
}

trait TagFileReader {
	
	def read(file:String) : Seq[String] = {
		Source.fromURL(
				getClass.getResource(file)).
			getLines.toList.filter(line => ""!=line)
	}
}

