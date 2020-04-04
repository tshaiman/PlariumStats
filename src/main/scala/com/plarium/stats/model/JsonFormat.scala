package com.plarium.stats.model


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonWriter, RootJsonFormat}

object AnyJsonFormat extends JsonWriter[Any] {
  def write(x: Any): JsValue = x match {
    case n: Int => JsNumber(n)
    case s: String => JsString(s)
  }
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat1(Error)
  implicit val statsFormat: RootJsonFormat[GameEvent] = jsonFormat10(GameEvent)
  implicit  val mapFormat:JsonWriter[Any] = AnyJsonFormat

}



