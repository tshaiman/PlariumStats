

organization := "com.plarium.pdp"

name := "PlariumStats"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaHttpVersion = "10.1.11"
lazy val akkaStreamVersion = "2.5.17"
lazy val akkaVersion = "2.5.26"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "io.spray"          %% "spray-json" % "1.3.5",

  "com.github.scredis" %% "scredis" % "2.2.5",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  "org.scalactic" %% "scalactic" % "3.1.1" % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.dimafeng" %% "testcontainers-scala" % "0.33.0" % Test)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}

mainClass in Compile := Some("com.plarium.stats.PlariumStatsApp")
mainClass in assembly := Some("com.plarium.stats.PlariumStatsApp")
assemblyJarName in assembly := "plarium-stats-1.0.jar"