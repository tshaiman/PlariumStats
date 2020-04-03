package com.plarium.stats.pipeline

import akka.actor.{Actor, ActorLogging, Props}

object StatsAggregatorActor{
  def props():Props = Props[StatsAggregatorActor]

}

class StatsAggregatorActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => throw new IllegalStateException("Unkown event was sent to StatsAggregatorActor")
  }
}