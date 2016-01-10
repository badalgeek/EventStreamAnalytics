package io.EventStreamAnalytics.reporter.actor

import akka.actor.{ActorLogging, Actor}
import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.{XmlClientConfigBuilder, ClientNetworkConfig, ClientConfig}
import com.hazelcast.core.{IMap, HazelcastInstance}
import akka.pattern.pipe
import io.EventStreamAnalytics.reporter.service.Foo

import scala.concurrent.Future

/**
  * Created by badal on 1/10/16.
  */
class GetUserActor extends Actor with ActorLogging {

  import context.dispatcher

  var hazelcastClient: HazelcastInstance = getClient

  def getClient:HazelcastInstance = {
    val config = new XmlClientConfigBuilder().build
    config.getNetworkConfig.setConnectionAttemptLimit(100)
    HazelcastClient.newHazelcastClient(config)
  }

  override def receive = {
    case _ => {
      val map: IMap[String, Nothing] = hazelcastClient.getMap("Events")
      val resFut: Future[String] = Future {
        Integer.toString(map.size())
      }
      resFut pipeTo sender
    }
  }

}

object GetUserActor {
  case class Ok(id: Int)
  case class Create(foo: Foo)
}
