package io.eventStreamAnalytics.server.front.handler

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import io.eventStreamAnalytics.server.eventProcessor.KafkaEventActor
import spray.can.Http
import spray.can.server.Stats
import spray.http.HttpMethods._
import spray.http.MediaTypes._
import spray.http._
import spray.util._

import scala.concurrent.duration._

/**
  * Created by badal on 1/4/16.
  */
class EventHandler extends Actor {

  implicit val timeout: Timeout = 1.second
  import context.dispatcher

  val eventActor = context.actorOf(Props[KafkaEventActor], name="eventProcessor")

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self)

    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      //sender ! index
      val client = sender
      context.actorFor("/user/IO-HTTP/listener-0") ? Http.GetStats onSuccess {
        case x: Stats => client ! statsPresentation(x)
      }

    case r@HttpRequest(GET, Uri.Path("/events"), _, _, _) =>
      eventActor ! r.uri.query.toString()
      sender ! HttpResponse()
  }

  def statsPresentation(s: Stats) = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <body>
          <h1>HttpServer Stats</h1>
          <table>
            <tr><td>uptime:</td><td>{s.uptime.formatHMS}</td></tr>
            <tr><td>totalRequests:</td><td>{s.totalRequests}</td></tr>
            <tr><td>openRequests:</td><td>{s.openRequests}</td></tr>
            <tr><td>maxOpenRequests:</td><td>{s.maxOpenRequests}</td></tr>
            <tr><td>totalConnections:</td><td>{s.totalConnections}</td></tr>
            <tr><td>openConnections:</td><td>{s.openConnections}</td></tr>
            <tr><td>maxOpenConnections:</td><td>{s.maxOpenConnections}</td></tr>
            <tr><td>requestTimeouts:</td><td>{s.requestTimeouts}</td></tr>
          </table>
        </body>
      </html>.toString()
    )
  )

}
