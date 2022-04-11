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
      "/message/",
      { (request: Request, response: Response) =>

        
        val body      = request.body()
        val timestamp = Instant.now().toEpochMilli
        println(s"--> Received@$timestamp: data: $body")

        val message = AlertIngestJSON.deserialize(body)
        println(s"Deserialized data: $alert")

        sendAlert(alert, producer)

        "ok"
      }
    )

  }


  def sendStockInfo(
      alert: AlertIngest,
      producer: KafkaProducer[String, String]
  ): Unit = {
    val doc = AlertIngest.serialize(alert)

    // TODO send stock info into Kafka
    val record: ProducerRecord[String, String] =
      new ProducerRecord[String, String](Configuration.AlertTopic, doc)

    producer.send(record)
  }

}
