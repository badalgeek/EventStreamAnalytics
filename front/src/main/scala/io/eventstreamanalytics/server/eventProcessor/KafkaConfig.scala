package io.eventStreamAnalytics.server.eventProcessor

import java.util.Properties

import com.typesafe.config.ConfigFactory
import io.eventStreamAnalytics.server.eventProcessor.KafkaConfig._
import io.eventStreamAnalytics.server.front.Server

trait KafkaConfig extends Properties {

  private val consumerPrefixWithDot = prefix + "."
  private val allKeys = Seq(brokers, keySerializer, valueSerializer, clientId)

  lazy val typesafeConfig = Server.server.getRootConfig()

  allKeys.map { key =>
    if (typesafeConfig.hasPath(key))
      put(key.replace(consumerPrefixWithDot, ""), typesafeConfig.getString(key))
  }

  def getCustomString(key: String) = typesafeConfig.getString(key)
  def getCustomInt(key: String) = typesafeConfig.getInt(key)
}

object KafkaConfig {

  val prefix = "kafka"

  //example.producer.Producer keys
  val brokers = s"$prefix.bootstrap.servers"
  val keySerializer = s"$prefix.key.serializer"
  val valueSerializer = s"$prefix.value.serializer"
  val clientId = s"$prefix.client.id"

  def apply() = new KafkaConfig {}
}
