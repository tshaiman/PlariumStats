package com.plarium.stats.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives.{completeWith, get, parameter, path, _}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.plarium.stats.model._

import scala.concurrent.duration._

trait StatsRoutes extends JsonSupport {

  def system: ActorSystem

  def orchestatrorActor: ActorRef


  val statsRoute: Route = {
    path("average_player_age") {
      implicit val timeout: Timeout = Timeout(1.seconds)
      get {
        completeWith(implicitly[ToResponseMarshaller[AvgAgeResponse]]) { f =>
          val request = AvgAgeRequestWithCallback(AvgAgeRequest, f)
          //sending the request to the orchestrator which will create actor-per-request
          //that will complete the Completable
          orchestatrorActor ! request
        }
      }
    } ~ path("average_level_per_country") {
      implicit val timeout: Timeout = Timeout(1.seconds)
      get {
        parameter('country_code.?) { maybeCountry =>
          //if (maybeCountry.isDefined) complete(StatusCodes.OK, s"avg_level_per_country ${maybeCountry.get}") else complete(StatusCodes.OK, s"avg_level_per_country no-val")
          completeWith(implicitly[ToResponseMarshaller[CountryMaxLevelResponse]]) { f =>
            val countryMaxLevelRequest = CountryMaxLevelRequest(maybeCountry)
            val request = CountryMaxLevelWithCallback(countryMaxLevelRequest, f)
            //sending the request to the orchestrator which will create actor-per-request
            //that will complete the Completable
            orchestatrorActor ! request
          }
        }
      }
    }
  }

}