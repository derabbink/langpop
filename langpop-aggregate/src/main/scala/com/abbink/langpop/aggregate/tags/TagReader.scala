package com.abbink.langpop.aggregate.tags

import java.io.File
import akka.actor.Actor
import scala.io.Source
import com.abbink.langpop.aggregate.Aggregator

object TagReader {
	sealed trait TagReaderMessage
	case class ReadFile(file : String) extends TagReaderMessage
}

class TagReader extends Actor with TagFileReader {
	import TagReader._
	
	def receive = {
		case message : TagReaderMessage => message match {
			case ReadFile(f) =>
				var tags = read(f)
				sender ! new Aggregator.TagSeq(tags)
				context.stop(self)
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

