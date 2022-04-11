package io.univalence.microservice.process

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

object ProcessMain {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {

    // TODO instantiate a stock repository from a Cassandra session

    val session = CqlSession.builder().build()

    val alertRepository: AlertRepository = new CassandraAlertRepository(session)
    val personneRepository: PersonneRepository = new CassandraPersonneRepository(session)

    // TODO create a consumer and subscribe to Kafka topic

    val consumer =
      new KafkaConsumer[String, String](
        Map[String, AnyRef](
          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> Configuration.KafkaBootstrap,
          ConsumerConfig.GROUP_ID_CONFIG          -> "process-3",
          ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
        ).asJava,
        new StringDeserializer,
        new StringDeserializer
      )

    consumer.subscribe(List(Configuration.AlertTopic).asJava)

    while (true) {
      // Récupère depuis Kafka
      val alertToken: List[AlertIngestToken] = nexAlertToken(consumer)

      // Regarde dans la db si le produit existe (ici on veut récupérer les personnes depuis le token)

      val newAlerts: List[AlertPersonne] =
        alertToken.map { alert =>
          val personne =
            personneRepository
              .findFromToken(alert.token)
              .getOrElse(// drop ?)

          aggregateWithPersonne(alert, personne)
        }

      alertRepository.saveAll(newAlerts)
    }
  }

  def aggregateWithPersonne(
      alertToken: AlertIngestToken,
      personne: Personne
  ): alertPersonne = {

    AlertPersonne(
        user_id = personne.idPersonne,
        user_name = personne.user_name,
        timestamp = alertToken.timestamp,
        reason = alertToken.reason,
        coordinates = alertToken.coordinates
      )

    if (stockInfo.stockType == StockInfo.STOCK)
      ProjectedStock(
        id = stockInfo.id,
        timestamp = stockInfo.timestamp,
        quantity = stockInfo.quantity
      )
    else
      projectedStock.copy(quantity =
        projectedStock.quantity + stockInfo.quantity
      )
  }

  def nextAlertToken(consumer: KafkaConsumer[String, String]): List[AlertIngestToken] = {
    val records: Iterable[ConsumerRecord[String, String]] =
      consumer.poll(java.time.Duration.ofSeconds(5)).asScala

    records.map { record =>
      println(s"Got record: $record")
      val doc = record.value()
      StockInfoJson.deserialize(doc)
    }.toList
  }

}
