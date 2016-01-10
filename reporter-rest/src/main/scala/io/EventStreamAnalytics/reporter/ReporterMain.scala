package io.EventStreamAnalytics.reporter

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import io.EventStreamAnalytics.reporter.service.EventStreamReporterActor
import spray.can.Http

/**
  * Created by badal on 1/10/16.
  */
object ReporterMain {

  def main(args: Array[String]) = {
    val root = ConfigFactory.load()
    val one = root.getConfig("EventStreamAnalyticsReporter")

    implicit val system = ActorSystem("EventStreamAnalyticsReporter", one)

    val service = system.actorOf(Props[EventStreamReporterActor], "demo-service")

    IO(Http) ! Http.Bind(service, "localhost", port = 8081)
  }
}
