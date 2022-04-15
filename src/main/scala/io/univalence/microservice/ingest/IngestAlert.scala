package io.univalence.microservice.ingest

import io.univalence.microservice.common._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import spark.Spark._
import spark.{Request, Response}

import java.time.Instant

object IngestAlert {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    port(Configuration.IngestAlertHttpPort)

    val producer: KafkaProducer[String, String] =
      new KafkaProducer[String, String](
        Map[String, AnyRef](
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> Configuration.KafkaBootstrap
        ).asJava,
        new StringSerializer,
        new StringSerializer
      )

    post(
      "/alert/",
      { (request: Request, response: Response) =>

        
        val body      = request.body()
        val timestamp = Instant.now().toEpochMilli

        val tokenId = null // Extract tokenId here
        println(s"--> Received@$timestamp: data: $body")

        val alert = AlertIngestJson.deserialize(body)
        println(s"Deserialized data: $alert")


        val alertToken = alertToAlertToken(alert,tokenId)

        sendAlert(alertToken, producer)

        "ok"

      }
    )

  }

  //Permet d'ajouter le tokenId utilisateur
  def alertToAlertToken(alert : AlertIngest, tokenId : String):
    AlertIngestToken = {
      AlertIngestToken(
        tokenId = tokenId,
        coordinates = alert.coordinates,
        timestamp = alert.timestamp,
        reason = alert.reason
      )
  }


  def sendAlert(
      alert: AlertIngestToken,
      producer: KafkaProducer[String, String]
  ): Unit = {
    val doc = AlertIngestTokenJson.serialize(alert)

    // TODO send stock info into Kafka
    val record: ProducerRecord[String, String] =
      new ProducerRecord[String, String](Configuration.AlertTopic, doc)

    producer.send(record)
  }

}
