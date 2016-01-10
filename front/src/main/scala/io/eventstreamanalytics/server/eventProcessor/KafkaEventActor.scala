package io.eventStreamAnalytics.server.eventProcessor

import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by badal on 1/6/16.
  */
class KafkaEventActor extends Actor with ActorLogging {

  private val producer = new KafkaProducer[String, String](KafkaConfig())

  def receive = {
    case x: String =>
        val y = x + "&id=" + UUID.randomUUID()
        log.debug("Received Event :" + y)
        producer.send(new ProducerRecord("events", y))
  }

  override def postStop(): Unit = producer.close()

}
