package com.plarium.stats.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

import scala.concurrent.ExecutionContext



class HttpServer(port:Int,orchestrator:ActorRef)
                (implicit actorSystem: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends StatsRoutes {

  override def system: ActorSystem = implicitly
  override def orchestatrorActor: ActorRef = orchestrator

  val route: Route = statsRoute

  def start(): Unit = {
    println(s"Server online at http://localhost:$port")
    Http().bindAndHandle(route, "localhost", port)
  }
}
