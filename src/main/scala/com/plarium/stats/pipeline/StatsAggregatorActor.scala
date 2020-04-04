package com.plarium.stats.pipeline

import java.time.Instant
import java.time.temporal.ChronoField

import akka.actor.{Actor, ActorLogging, Props}
import com.plarium.stats.model._

import scala.math._

object StatsAggregatorActor {
  def props(): Props = Props[StatsAggregatorActor]
  def time(): Long = Instant.now.getLong(ChronoField.MILLI_OF_SECOND)
}

class StatsAggregatorActor extends Actor with ActorLogging {
  import StatsAggregatorActor._
  var users_metadata: Map[String, Int] = Map.empty // Map [uuid -> max_level]
  var age_avg: Double = 0
  var fetched: Long = 0
  var level_countries: Map[String, Int] = Map.empty

  override def receive: Receive = {
    case evt: GameEvent => handleEvent(evt)

    case AvgAgeRequest =>
      log.info(s"Request to get average user aga was received")
      sender() ! get_avg_age()

    case req:CountryMaxLevelRequest =>
      log.info(s"Request to get country max level was received")
      sender() ! get_level_by_countries(req.countryCode)

    case _ => throw new IllegalStateException("Unknown event was sent to StatsAggregatorActor")
  }

  def handleEvent(evt: GameEvent): Unit = {
    fetched = fetched + 1
    update_users_meta(evt)
    update_level_countries(evt)

  }
  /**
  * update levelr countries
  */
  def update_level_countries(evt: GameEvent): Unit = {
    val (code, level) = (evt.country_code, evt.last_game_level)
    if (!level_countries.contains(code)) {
      //update average
      level_countries += (code -> level)
    } else {
      //update max game level
      level_countries = level_countries.updated(evt.country_code, max(level_countries.getOrElse(code, -1), level))
    }
  }

  /**
   * update users metadata map and the average age
   */
  def update_users_meta(evt: GameEvent): Unit = {
    if (!users_metadata.contains(evt.user_uuid)) {
      //update average
      val n = users_metadata.size
      age_avg = ((age_avg * n) + evt.age) / (n + 1)
      //update users_metadata
      users_metadata += (evt.user_uuid -> evt.last_game_level)

    } else {
      //update game level
      users_metadata = users_metadata.updated(evt.user_uuid, evt.last_game_level)
    }
  }

  /********** Responses ***************************/
  def get_avg_age(): AvgAgeResponse = {
    AvgAgeResponse(age_avg.toInt,time(),fetched)
  }

  def get_level_by_countries(countryCode: Option[String]): CountryMaxLevelResponse = {
    countryCode match {
      case Some(code) =>CountryMaxLevelResponse(level_countries.filter(x=>x._1.equals(code)), time(), fetched)
      case None => CountryMaxLevelResponse(level_countries, time(), fetched)
    }
  }
}