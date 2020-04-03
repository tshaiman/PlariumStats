organization := "com.plarium.pdp"

name := "PlariumStats"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaHttpVersion = "10.1.11"
lazy val akkaStreamVersion = "2.5.26"
lazy val akkaVersion = "2.5.23"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "io.spray"          %% "spray-json" % "1.3.5",

  "com.github.scredis" %% "scredis" % "2.2.5",
  "com.google.cloud" % "google-cloud-logging-logback" % "0.95.0-alpha",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",

  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  "org.scalactic" %% "scalactic" % "3.1.1" % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.dimafeng" %% "testcontainers-scala" % "0.33.0" % Test)

mainClass in Compile := Some("com.plarium.pdp.ml.online.ModelBalancerServer")
