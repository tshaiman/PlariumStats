import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.stream.scaladsl.Keep
import spray.json._
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json.DefaultJsonProtocol._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

final case class SampleData(name: String, value: Int)

object SampleDataSprayProtocol extends DefaultJsonProtocol {
  implicit val sampleDataProtocol: RootJsonFormat[SampleData] = jsonFormat2(SampleData)
}

object WebServer {

  case class Bid(userId: String, offer: Int)
  case object GetBids
  case class Bids(bids: List[Bid])

  class Auction extends Actor with ActorLogging {
    var bids = List.empty[Bid]

    def receive = {
      case bid@Bid(userId, offer) =>
        bids = bids :+ bid
        log.info(s"Bid complete: $userId, $offer")
      case GetBids => sender() ! Bids(bids)
      case _ => log.info("Invalid message")
    }
  }

  implicit val bidFormat = jsonFormat2(Bid)
  implicit val bidsFormat = jsonFormat1(Bids)

  def business(data:SampleData):Future[Unit] = Future {
    println(data.name)
  }

  def main(args:Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val resumeOnParsingException = ActorAttributes.withSupervisionStrategy {
      new akka.japi.function.Function[Throwable, Supervision.Directive] {
        override def apply(t: Throwable): Supervision.Directive = t match {
          case _: spray.json.JsonParser.ParsingException => Supervision.Resume
          case _: spray.json.DeserializationException => Supervision.Resume
          case _ => Supervision.stop
        }
      }
    }


    val config = system.settings.config.getConfig("our-kafka-consumer")
    val consumerSettings = ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
    val topic = config.getString("topic")
    val committerSettings = CommitterSettings(system)
    import SampleDataSprayProtocol._
    val control : Consumer.DrainingControl[Done]=
      Consumer
        .committableSource(consumerSettings, Subscriptions.topics("tomers"))
        .mapAsync(10) { msg =>
          val value:String = msg.record.value()
          val sampleData = value.parseJson.convertTo[SampleData]
          business(sampleData)
            .map(_ => msg.committableOffset)
        }.withAttributes(resumeOnParsingException)
        .toMat(Committer.sink(committerSettings))(Keep.both)
        .mapMaterializedValue(DrainingControl.apply)
        .run()

    print("waiting")
    //Thread.sleep(60000)

  }
  def main2(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher


    val route =
      path("hello") {
        concat(
          get {
            parameter('user,'shouldFilter.as[Boolean]) { (user, shouldFilter) =>
                complete(StatusCodes.OK, s"hello $user $shouldFilter")
            }
          }
        )
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }
}