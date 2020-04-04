package com.plarium.stats.rest

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.plarium.stats.model.{AvgAgeRequest, AvgAgeRequestWithCallback, AvgAgeResponse, CountryMaxLevelResponse, CountryMaxLevelWithCallback, Error}

object RequestActor {
  def props(statsActor:ActorRef):Props = Props(new RequestActor(statsActor))
}
class RequestActor(statsActor:ActorRef) extends Actor with ActorLogging {

  var avgRequest : AvgAgeRequestWithCallback = null
  var maxCountryRequest: CountryMaxLevelWithCallback = null
  val RequestTimeoutResponse = Error("Request timeout")

  override def receive: Receive = {

    case req:AvgAgeRequestWithCallback =>
      avgRequest = req
      statsActor ! AvgAgeRequest

    case req:CountryMaxLevelWithCallback =>
      maxCountryRequest = req
      statsActor ! req.request

    case avgAgeResponse: AvgAgeResponse =>
      avgRequest.complete(avgAgeResponse)
      context.stop(self)

    case maxLevelResponse: CountryMaxLevelResponse =>
      maxCountryRequest.complete(maxLevelResponse)
      context.stop(self)

  }

}