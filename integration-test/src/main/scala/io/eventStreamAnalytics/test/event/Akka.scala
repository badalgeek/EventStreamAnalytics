package io.eventStreamAnalytics.test.event

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by sbadal on 1/10/16.
  */
object Akka {

  private val root = ConfigFactory.load()
  private val one = root.getConfig("EventStreamAnalyticsIT")
  val system = ActorSystem("Test-Akka", one)

}
