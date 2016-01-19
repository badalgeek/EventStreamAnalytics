package io.eventStreamAnalytics.server.eventProcessor

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

import akka.actor.{Actor, ActorLogging}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

/**
  * Created by badal on 1/6/16.
  */
class KafkaEventActor extends Actor with ActorLogging {

  private val producer = new KafkaProducer[String, String](KafkaConfig())

  private val format: SimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.hh:mm:ss:SSS:")
  private val random = new Random()

  def receive = {
    case x: String =>
      val y = x + "&id=" + format.format(new Date()) + random.nextInt(10000)
        log.debug("Received Event :" + y)
        producer.send(new ProducerRecord("events", y))
  }

  override def postStop(): Unit = producer.close()

}
