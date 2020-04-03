package com.plarium.stats.model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat1(Error)
  implicit val statsFormat: RootJsonFormat[GameEvent] = jsonFormat10(GameEvent)
}

