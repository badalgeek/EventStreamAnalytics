package io.eventStreamAnalytics.test.event

import spray.client.pipelining._
import spray.http.HttpResponse
import scala.concurrent.{Await, Future}
import scala.util.{Success, Failure}
import scala.concurrent.duration._
/**
  * Created by badal on 1/9/16.
  */
class ReportingProcessor extends Pipeline {

  import system.dispatcher
  import system.log

  def processRequest(query: String) = {
    val request = Get(query)
    pipeline.flatMap(_ (request))
  }

  def processRequestAndGetBody(query: String): String = {
    val responseFuture: Future[HttpResponse] = processRequest(query)
    Await.result(responseFuture, 50000 millis)
    log.debug("Done waiting")
    responseFuture.value.get.get.entity.asString
  }

  override def getPort(): Int = 9000

  override def getHost(): String = "localhost"
}
