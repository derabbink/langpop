package com.abbink.langpop.query.aggregator

import akka.actor.Actor

/**
 * every aggregator should have one of these to throttle requests.
 */
trait ApiGateway extends Actor {

}
