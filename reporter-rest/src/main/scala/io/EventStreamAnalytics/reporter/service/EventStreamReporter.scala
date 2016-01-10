package io.EventStreamAnalytics.reporter.service

import java.util.concurrent.TimeUnit

import io.EventStreamAnalytics.reporter.actor.GetUserActor
import akka.dispatch.sysmsg.Create

import akka.actor.{ Actor, Props }
import akka.pattern.ask
import akka.util.Timeout
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{ read, write }
import spray.httpx.Json4sSupport
import spray.routing._
import spray.http.StatusCodes._

import scala.concurrent.duration.FiniteDuration

/**
  * Created by badal on 1/10/16.
  */
class EventStreamReporterActor extends Actor with EventStreamReporterService {

  def actorRefFactory = context

  def receive = runRoute(eventStreamReporterRoute)

}

object Json4sProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
}

case class Foo(bar: String)

trait EventStreamReporterService extends HttpService {

  import Json4sProtocol._
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout.apply(FiniteDuration.apply(10L, TimeUnit.SECONDS))

  val worker = actorRefFactory.actorOf(Props[GetUserActor], "getUserActor")

  val eventStreamReporterRoute = {
    get {
      path("user") {
        get {
          respondWithStatus(Created) {
            entity(as[Foo]) { someObject =>
              doCreate(someObject)
            }
          }
          complete(
            (worker ? "").map(result => s"I got a response: ${result}")
          )
        }
      }
    }
  }

  def doCreate[T](foo: Foo) = {
    complete {
      (worker ? GetUserActor.Create(foo))
        .mapTo[GetUserActor.Ok]
        .map(result => s"I got a response: ${result}")
        .recover { case _ => "error" }
    }
  }

}