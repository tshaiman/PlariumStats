
import spray.json._
import DefaultJsonProtocol._

var countries:Map[String,Int] = Map.empty

countries += ("NY"->10)
countries += ("AZ"->14)



//print(countries)
print(countries.toJson)


case class Response2(countries:Map[String,Int],ts:Long)
implicit val responseFormat = jsonFormat2(Response2)
val res = Response2(countries,1585997003)
print(res.toJson)
//val str = countries.map(v=> s""""${v._1}":${v._2}""").toArray