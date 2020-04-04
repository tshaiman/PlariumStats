package com.plarium.stats.pipeline

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Keep
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import com.plarium.stats.model.{GameEvent, JsonSupport, Start}
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait KConsumer {
  def startConsumer(sinkTo: ActorRef)(implicit mat: ActorMaterializer)
}

object AlpakaKafkaConsumer {
   def props(sinkTo: ActorRef)(implicit mat: ActorMaterializer): Props = Props(new AlpakaKafkaConsumer(sinkTo))
}

class AlpakaKafkaConsumer(sinkTo: ActorRef)(implicit mat: ActorMaterializer) extends Actor with ActorLogging
  with KConsumer
  with JsonSupport {

  override def receive: Receive = {
    case Start =>
      startConsumer(sinkTo)
  }

  //Alpakak - Akka Stream pipeline with commit after processing
  override def startConsumer(sinkTo: ActorRef)(implicit mat: ActorMaterializer): Unit = {

    val consumerConfig = context.system.settings.config.getConfig("our-kafka-consumer")
    val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
    val topic = consumerConfig.getString("topic")
    val committerSettings = CommitterSettings(context.system)

    //1. Define Parsing Exception Strategy
    //reference: https://doc.akka.io/docs/alpakka-kafka/current/serialization.html#spray-json
    val resumeOnParsingException = ActorAttributes.withSupervisionStrategy {
      case e:spray.json.JsonParser.ParsingException =>
        log.warning(s"Parsing Exception while Deserializing Json to GameEvent. ${e.detail}")
        Supervision.Resume
      case _ => Supervision.Resume
    }

    //2. Alpakka Akka Implementation for Consuming ,deserialize Json and  Committing Offset in bulks
    // Reference: https://doc.akka.io/docs/alpakka-kafka/current/consumer.html#committer-sink
      Consumer
        .committableSource(consumerSettings, Subscriptions.topics(topic))
        .mapAsync(1) { msg =>
          val value: String = msg.record.value()
          val eventData = value.parseJson.convertTo[GameEvent]
          handle(eventData)
            .map(_ => msg.committableOffset)
        }.withAttributes(resumeOnParsingException)
        .toMat(Committer.sink(committerSettings))(Keep.both)
        .mapMaterializedValue(DrainingControl.apply)
        .run()

    def handle(eventData: GameEvent): Future[Unit] = Future {
      sinkTo ! eventData
    }
  }
}
