import sbt._
import Keys._

object BuildSettings {

  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "io.badal",
    version := "1.0.0-M1",
    scalaVersion := "2.10.5",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases")
  )

  val buildSettings = commonSettings ++ Seq(
    crossScalaVersions := Seq("2.10.5"),
    scalacOptions ++= Seq("-target:jvm-1.7")
  )

  val buildSettingsJava = commonSettings ++ Seq(
    publishMavenStyle := true,
    crossPaths := false,
    autoScalaLibrary := false,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    testListeners in (Test,test) := Nil
  )
}

object Dependencies {

  private val sprayVersion = "1.3.1"
  private val kafkaVersion = "0.9.0.0"
  private val akkaVersion = "2.3.12"
  private val junitVersion = "4.12"

  val akka = Seq("akka-actor", "akka-slf4j").map("com.typesafe.akka" %% _ % akkaVersion)
  val logback = Seq("logback-classic").map("ch.qos.logback" % _ % "1.0.9")

  val spray = Seq("spray-can").map("io.spray" % _ % sprayVersion)
  val sprayClient = Seq("spray-client").map("io.spray" % _ % sprayVersion)
  val sprayJson = Seq("spray-json").map("io.spray" %% _ % sprayVersion)
  val sprayRouting = Seq("spray-routing").map("io.spray" % _ % sprayVersion)
  val json4s = Seq("json4s-native").map("org.json4s" %% _ % "3.3.0")
  val apacheCommon = Seq("commons-io").map("commons-io" % _ % "2.4")

  val kafka = Seq("kafka").map("org.apache.kafka" %% _ % kafkaVersion)
  val zookeeper = Seq("zookeeper").map("org.apache.zookeeper" % _ % "3.4.6")

  val guava = Seq("guava").map("com.google.guava" % _ % "19.0")

  val hazelCast = Seq("hazelcast-all").map("com.hazelcast" % _ % "3.5.4")

  val junit = Seq("junit").map("junit" % _ % junitVersion % Test)
  val junitInterface = Seq("junit-interface").map("com.novocode" % _ % "0.11" % Test)
  val scalaTest = Seq("scalatest").map("org.scalatest" % _ % "2.2.2" % Test)

}

object EventStreamAnalyticsBuild extends Build {

  import BuildSettings._
  import Dependencies._

  val appName = "event-stream-analytics"

  lazy val root: Project = Project(
    appName,
    file("."),
    settings = buildSettings
  ) .aggregate(publishedProjects: _*)

  lazy val front = Project(
    "event-stream-analytics-front",
    file("front"),
    settings = buildSettings
  ).settings(libraryDependencies ++= spray ++ kafka ++ akka ++ guava)

  lazy val reporter = Project(
    "event-stream-analytics-reporter-rest",
    file("reporter-rest"),
    settings = buildSettings
  ).settings(libraryDependencies ++= spray ++ sprayRouting ++ sprayJson ++ akka ++ hazelCast ++ json4s)

  lazy val worker = Project(
    "event-stream-analytics-worker",
    file("worker-node"),
    settings = buildSettingsJava
  ).settings(libraryDependencies ++= kafka ++ akka ++ guava ++ hazelCast)

  lazy val integrationTest = Project(
    "event-stream-analytics-integration-test",
    file("integration-test"),
    settings = buildSettings
  ).settings(
      crossPaths := false,
      parallelExecution in Test := false,
      libraryDependencies ++= zookeeper ++ junit ++ junitInterface ++ sprayClient ++ sprayJson ++ apacheCommon ++ logback
    )
    .dependsOn(front)
    .dependsOn(worker)
    .dependsOn(reporter)

  lazy val publishedProjects = Seq[ProjectReference](
    front,
    worker,
    reporter,
    integrationTest
  )
}