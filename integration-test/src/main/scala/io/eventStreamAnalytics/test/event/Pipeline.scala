package io.eventStreamAnalytics.test.event

import java.util.concurrent.TimeUnit

import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.client.pipelining._
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
  * Created by sbadal on 1/10/16.
  */
trait Pipeline {

  implicit  val system = Akka.system
  import system.dispatcher

  implicit val timeout = Timeout.apply(FiniteDuration.apply(10L, TimeUnit.SECONDS))

  def getPort(): Int
  def getHost(): String

  val pipeline: Future[SendReceive] = {
    for (
      Http.HostConnectorInfo(connector, _) <-
      IO(Http) ? Http.HostConnectorSetup(getHost(), port = getPort())
    ) yield sendReceive(connector)
  }
}
