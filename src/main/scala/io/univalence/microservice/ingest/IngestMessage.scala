package io.univalence.microservice.ingest

import io.univalence.microservice.common._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import spark.Spark._
import spark.{Request, Response}

import java.time.Instant

object IngestMessage {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    port(Configuration.IngestMessageHttpPort)

    val producer: KafkaProducer[String, String] =
      new KafkaProducer[String, String](
        Map[String, AnyRef](
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> Configuration.KafkaBootstrap
        ).asJava,
        new StringSerializer,
        new StringSerializer
      )

    post(
      "/message",
      { (request: Request, response: Response) =>

        val body      = request.body()

        val timestamp = Instant.now().toEpochMilli
        println(s"--> Received@$timestamp: data: $body")

        // Extract token
        val token = null

        val messageVar = MessageIngestJson.deserialize(body)
        println(s"Deserialized data: $messageVar")

        val messageToken = messageToMessageToken(messageVar,token)
        sendMessage(messageToken, producer)

        "ok"
      }
    )

  }

 //Permet d'ajouter le token utilisateur
  def messageToMessageToken(messageVar : MessageIngest, token : String):
    MessageIngestToken = {
      MessageIngestToken(
        token = token,
        coordinates = messageVar.coordinates,
        timestamp = messageVar.timestamp,
        message = messageVar.message
      )
  }

  def sendMessage(
      messageVar: MessageIngestToken,
      producer: KafkaProducer[String, String]
  ): Unit = {
    val doc = MessageIngestTokenJson.serialize(messageVar)

    // TODO send stock info into Kafka
    val record: ProducerRecord[String, String] =
      new ProducerRecord[String, String](Configuration.MessageTopic, doc)

    producer.send(record)
  }

}
