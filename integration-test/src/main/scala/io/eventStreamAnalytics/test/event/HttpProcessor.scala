package io.eventStreamAnalytics.test.event

import java.util.concurrent.TimeUnit

import akka.event.Logging
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.client.pipelining._
import akka.pattern.ask
import spray.http.HttpResponse
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by badal on 1/10/16.
  */
trait HttpProcessor {

  implicit val system = Akka.system
  import system.dispatcher

  var log = Logging(system.eventStream, this.getClass.getName)

  implicit val timeout = Timeout.apply(FiniteDuration.apply(10L, TimeUnit.SECONDS))

  def getPort(): Int
  def getHost(): String

  val pipeline: Future[SendReceive] = {
    for (
      Http.HostConnectorInfo(connector, _) <-
      IO(Http) ? Http.HostConnectorSetup(getHost(), port = getPort())
    ) yield sendReceive(connector)
  }

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
}
