package com.plarium.stats.model

case object Start

final case class Error(msg: String)

final case class GameEvent(firstName: String,
                              lastName: String,
                              age: Int,
                              last_game_level: Int,
                              level_attempts: Int,
                              email: String,
                              event_ts: Long,
                              country_code: String,
                              is_depositor: Boolean,
                              user_uuid: String)
trait RestRequest

trait RestResponse {
  val time: Long
  val fetched_records: Long
}


//Request /Response Scenarios
case object AvgAgeRequest extends RestRequest
final case class AvgAgeResponse(average_players_age:Int,time:Long,fetched_records:Long) extends RestResponse

case class CountryMaxLevelRequest(countryCode:Option[String]) extends RestRequest
case class CountryMaxLevelResponse(countries:Map[String,Int], time:Long, fetched_records:Long) extends RestResponse

trait RequestqWithCallback
//Routing & Actor-Per-Request Pattern
case class AvgAgeRequestWithCallback(request:RestRequest,
                                   complete: AvgAgeResponse => Unit) extends RequestqWithCallback

case class CountryMaxLevelWithCallback(request:RestRequest,
                                     complete: CountryMaxLevelResponse => Unit) extends RequestqWithCallback