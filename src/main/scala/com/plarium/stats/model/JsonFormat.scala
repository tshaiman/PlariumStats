package com.plarium.stats.model
import spray.json._
import DefaultJsonProtocol._


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonWriter, RootJsonFormat}

//object AnyJsonFormat extends JsonWriter[Any] {
//  def write(x: Any): JsValue = x match {
//    case n: Int => JsNumber(n)
//    case s: String => JsString(s)
//  }
//}

//case class Response2(countries:Map[String,Int],ts:Long)


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  //implicit  val mapFormat:JsonWriter[Any] = AnyJsonFormat
  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat1(Error)
  implicit val statsFormat: RootJsonFormat[GameEvent] = jsonFormat10(GameEvent)
  implicit val avgResponseFormat = jsonFormat3(AvgAgeResponse)
  implicit val maxCountyLevelFormat = jsonFormat3(CountryMaxLevelResponse)
  //implicit val responseFormat = jsonFormat2(Response2)
  //implicit val maxCountryRequetFormat = jsonFormat1(CountryMaxLevelRequest)


}



