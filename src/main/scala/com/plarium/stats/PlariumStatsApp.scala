package com.plarium.stats


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.plarium.stats.model.Start
import com.plarium.stats.pipeline.Orchestrator
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

object PlariumStatsApp extends App{
  implicit val system: ActorSystem = ActorSystem("Plarium-Stats")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val orchestrator = system.actorOf(Orchestrator.props())
  startHttpServer(system.settings.config)
  addShutdownHook()

  //Start The pipeline
  orchestrator ! Start

  def startHttpServer(config:Config):Unit = {
    val port = config.getInt("application.server-port")
    print(port)
    //new HttpServer(port,orchestrator).start()
  }


  def addShutdownHook(): Unit = {
    sys.addShutdownHook(() => {
      println("shutting down Panda Stats Counter")
      val future = system.terminate()
      Await.result(future, 30 seconds)
    })
  }
}
