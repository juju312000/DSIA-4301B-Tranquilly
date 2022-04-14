package io.univalence.microservice.process

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

object ProcessMessage {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {

    // TODO instantiate a stock repository from a Cassandra session

    val session = CqlSession.builder().build()

    val messageRepository: MessageRepository = new CassandraMessageRepository(session)
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

    consumer.subscribe(List(Configuration.MessageTopic).asJava)

    while (true) {
      // Récupère depuis Kafka
      val messageToken: List[MessageIngestToken] = nexMessageToken(consumer)

      // Regarde dans la db si le produit existe (ici on veut récupérer les personnes depuis le token)

      val newMessages: List[MessagePersonne] =
        messageToken.map { message =>
          val personne =
            personneRepository
              .findFromToken(message.token)
              //.getOrElse(// drop ?)

          aggregateWithPersonne(message, personne)
          // Envoi de la notif
          
        }

      messageRepository.saveAll(newMessages)
    }
  }

  def aggregateWithPersonne(
      messageToken: MessageIngestToken,
      personne: Personne
  ): MessagePersonne = {

    MessagePersonne(
        user_id = personne.idPersonne,
        user_name = personne.user_name,
        timestamp = messageToken.timestamp,
        message = messageToken.message,
        coordinates = messageToken.coordinates
      )
  }

  def nextMessageToken(consumer: KafkaConsumer[String, String]): List[MessageIngestToken] = {
    val records: Iterable[ConsumerRecord[String, String]] =
      consumer.poll(java.time.Duration.ofSeconds(5)).asScala

    records.map { record =>
      println(s"Got record: $record")
      val doc = record.value()
      MessageIngestTokenJson.deserialize(doc)
    }.toList
  }

}
