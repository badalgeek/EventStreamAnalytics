package io.eventStreamAnalytics.test.event

/**
  * Created by badal on 1/9/16.
  */
class EventProcessor extends HttpProcessor {

  override def getPort(): Int = 8086

  override def getHost(): String = "localhost"
}
