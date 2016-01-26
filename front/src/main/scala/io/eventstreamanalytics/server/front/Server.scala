package io.eventStreamAnalytics.server.front

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.{Config, ConfigFactory}
import io.eventStreamAnalytics.server.front.handler.EventHandler
import org.apache.commons.cli.{DefaultParser, CommandLineParser, Options, CommandLine}
import spray.can.Http

/**
  * Created by badal on 1/4/16.
  */
object Server {

  var server: Server = null

  def main(args: Array[String]) = {
    server = new Server()
    server.start(args)
  }
}

class Server() {

  val CONFIG: String = "config"
  var rootConfig: Config = null

  def start(args: Array[String]) = {
    val commandLine = getCommandLine(args)
    if(commandLine.hasOption(CONFIG)) {
      rootConfig = ConfigFactory.parseFile(new File(commandLine.getOptionValue(CONFIG)))
    } else {
      rootConfig = ConfigFactory.load("eventStreamFront.conf")
    }
    val frontConfig = rootConfig.getConfig("EventStreamAnalyticsFront")

    implicit val system = ActorSystem("EventStreamAnalyticsFront", frontConfig)
    // the handler actor replies to incoming HttpRequests
    val handler = system.actorOf(Props[EventHandler], name = "handler")

    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8086)
  }

  def getCommandLine(args: Array[String]): CommandLine = {
    val options: Options = new Options
    options.addOption(CONFIG, true, "Config File Full Path")
    val parser: CommandLineParser = new DefaultParser
    parser.parse(options, args)
  }

  def getRootConfig(): Config = {
    this.rootConfig
  }
}
