package io.eventStreamAnalytics.server.eventProcessor

import java.text.SimpleDateFormat
import java.time.{ZonedDateTime, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

/**
  * Created by badal on 1/6/16.
  */
class KafkaEventActor extends Actor {

  private val producer = new KafkaProducer[String, String](KafkaConfig())
  val log = Logging(context.system.eventStream, this.getClass.getName)

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.hh:mm:ss:SSS")

  private val random = new Random()

  def receive = {
    case x: String =>
      val date = LocalDateTime.now()
      var y = x + "&id=" + date.format(formatter) + ":" + random.nextInt(10000)
      y += "&date=" + date.format(formatter)

      log.debug("Received Event :" + y)
      producer.send(new ProducerRecord("events", y))
  }

  override def postStop(): Unit = producer.close()

}
