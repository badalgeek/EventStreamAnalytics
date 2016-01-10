package io.eventStreamAnalytics.test.event

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import spray.client.pipelining._

import scala.concurrent.duration.FiniteDuration

/**
  * Created by badal on 1/9/16.
  */
class EventProcessor extends Pipeline {

  import system.dispatcher

  def processRequest(query: String) = {
    val request = Get(query)
    pipeline.flatMap(_ (request))
  }

  override def getPort(): Int = 8080

  override def getHost(): String = "localhost"
}
