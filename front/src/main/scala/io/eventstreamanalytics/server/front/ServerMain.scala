package io.eventStreamAnalytics.server.front

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import io.eventStreamAnalytics.server.front.handler.EventHandler
import spray.can.Http

/**
  * Created by badal on 1/4/16.
  */
object ServerMain {

  def main(args: Array[String]) = {
    val root = ConfigFactory.load()
    val one = root.getConfig("EventStreamAnalyticsFront")

    implicit val system = ActorSystem("EventStreamAnalyticsFront", one)
    // the handler actor replies to incoming HttpRequests
    val handler = system.actorOf(Props[EventHandler], name = "handler")

    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)
  }
}
