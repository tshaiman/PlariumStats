package com.plarium.stats.pipeline

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.ActorMaterializer
import com.plarium.stats.model.Start

object Orchestrator {
  def props()(implicit mat: ActorMaterializer):Props = Props(new Orchestrator())
}


/***
 * Orchestrator Actor is the "glue" of the application pipeline and routing requests.
 * it is the Root actor of our application and it is responsible to build the hierarchy and govern it.
 * it is also helping in achieving the "actor-per-request" pattern.
 */
class Orchestrator()(implicit mat:ActorMaterializer) extends Actor with ActorLogging{

  val aggregator: ActorRef = context.actorOf(StatsAggregatorActor.props())
  val consumer: ActorRef = context.actorOf(AlpakaKafkaConsumer.props(aggregator))

  override def receive: Receive = {

    case Start =>
      consumer ! Start

  }
}

