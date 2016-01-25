package io.eventStreamAnalytics.test.event
/**
  * Created by badal on 1/9/16.
  */
class ReportingProcessor extends HttpProcessor {

  override def getPort(): Int = 9000

  override def getHost(): String = "localhost"
}
