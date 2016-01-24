import sbt._
import Keys._

object BuildSettings {

  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "io.badal",
    version := "1.0.0-M1",
    scalaVersion := "2.10.5",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += "opensourceagility-release" at "http://repository-opensourceagility.forge.cloudbees.com/release/",
    resolvers += "aws-release" at "http://dynamodb-local.s3-website-us-west-2.amazonaws.com/release"
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

  val logback = Seq("ch.qos.logback" % "logback-classic" % "1.1.3")

  val spray = Seq("spray-can").map("io.spray" % _ % sprayVersion)
  val sprayClient = Seq("spray-client").map("io.spray" % _ % sprayVersion)
  val sprayJson = Seq("spray-json").map("io.spray" %% _ % sprayVersion)

  val apacheCommon = Seq("commons-io").map("commons-io" % _ % "2.4")
  val apacheLang = Seq("commons-lang3").map("org.apache.commons" % _ % "3.4")

  val kafka = Seq("kafka").map("org.apache.kafka" %% _ % kafkaVersion)
  val zookeeper = Seq("zookeeper").map("org.apache.zookeeper" % _ % "3.4.6")

  val guava = Seq("guava").map("com.google.guava" % _ % "19.0")

  val hazelCast = Seq("hazelcast-all").map("com.hazelcast" % _ % "3.5.4")
//  val dynamoDBClient = Seq("aws-java-sdk-dynamodb").map("com.amazonaws" % _ % "1.10.47")

  val springBoot = Seq("spring-boot-starter-data-rest", "spring-boot-starter-data-mongodb").map("org.springframework.boot" % _ % "1.3.1.RELEASE")
  val spring = Seq("spring-data-rest-webmvc").map("org.springframework.data" % _ % "2.4.2.RELEASE")
  val springCore = Seq("spring-data-commons").map("org.springframework.data" % _ % "1.11.2.RELEASE")
  val springUI = Seq("spring-data-rest-hal-browser").map("org.springframework.data" % _ % "2.4.2.RELEASE")
//  val springDynamoDb = Seq("spring-data-dynamodb").map("org.socialsignin" % _ % "4.2.1")
//  val dynamoDB = Seq("DynamoDBLocal").map("com.amazonaws" % _ % "1.10.5.1" % Test)

  val junit = Seq("junit").map("junit" % _ % junitVersion % Test)
  val junitInterface = Seq("junit-interface").map("com.novocode" % _ % "0.11" % Test)
  val scalaTest = Seq("scalatest").map("org.scalatest" % _ % "2.2.2" % Test)

  val mangoDBEmbedded = Seq("de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.2")
  val mangoDbClient = Seq("org.mongodb" % "mongo-java-driver" % "3.2.1")

  val apacheCli = Seq("commons-cli" % "commons-cli" % "1.3.1")
}

object EventStreamAnalyticsBuild extends Build {

  import BuildSettings._
  import Dependencies._

  val appName = "event-stream-analytics"

  lazy val root: Project = Project(
    appName,
    file("."),
    settings = buildSettings
  ).aggregate(publishedProjects: _*)

  lazy val common = Project(
    "event-stream-analytics-common",
    file("common"),
    settings = buildSettingsJava
  ).settings(
    libraryDependencies ++=  guava ++ apacheLang ++ spring  ++ mangoDbClient
      ++ springBoot
  )

  lazy val front = Project(
    "event-stream-analytics-front",
    file("front"),
    settings = buildSettings
  ).settings(
    libraryDependencies ++= spray ++ kafka ++ akka ++ guava ++ apacheCli,
    excludeDependencies ++= Seq(
      SbtExclusionRule("log4j", "log4j"),
      SbtExclusionRule("org.slf4j", "slf4j-log4j12")
    )
  )

  lazy val reporter = Project(
    "event-stream-analytics-reporter-rest",
    file("reporter-rest"),
    settings = buildSettingsJava
  ).settings(
    libraryDependencies ++= spring ++ springUI ++ springBoot
  )
    .dependsOn(common)

  lazy val worker = Project(
    "event-stream-analytics-worker",
    file("worker-node"),
    settings = buildSettingsJava
  ).settings(
    libraryDependencies ++= kafka ++ akka ++ guava ++ hazelCast ++ mangoDbClient ++ apacheCli,
    excludeDependencies ++= Seq(
      SbtExclusionRule("log4j", "log4j"),
      SbtExclusionRule("org.slf4j", "slf4j-log4j12")
    )
  )
    .dependsOn(common)

  lazy val integrationTest = Project(
    "event-stream-analytics-integration-test",
    file("integration-test"),
    settings = buildSettings
  ).dependsOn(common)
    .dependsOn(front)
    .dependsOn(worker)
    .dependsOn(reporter)
    .settings(
      crossPaths := false,
      classpathTypes ++= Set("dylib"),
      parallelExecution in Test := false,
      libraryDependencies ++= zookeeper ++ junit ++ junitInterface ++ sprayClient ++ sprayJson ++ apacheCommon ++
        mangoDBEmbedded,
      excludeDependencies ++= Seq(
        SbtExclusionRule("log4j", "log4j"),
        SbtExclusionRule("org.slf4j", "slf4j-log4j12")
      )
    )


  lazy val publishedProjects = Seq[ProjectReference](
    front,
    worker,
    reporter,
    integrationTest
  )
}