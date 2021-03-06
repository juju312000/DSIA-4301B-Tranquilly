package io.univalence.microservice.process

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Instant

object ProcessAlert {

  import scala.jdk.CollectionConverters._

  // TODO instantiate a stock repository from a Cassandra session
  val session = CqlSession.builder().build()

  val alertRepository: AlertRepository = new CassandraAlertRepository(session)
  val personneRepository: PersonneRepository = new CassandraPersonneRepository(session)



  def main(args: Array[String]): Unit = {

    //val httpClient = new OkHttpClient.Builder().build()

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
      val alertToken: List[AlertIngestToken] = nextAlertToken(consumer)

      // Regarde dans la db si le produit existe (ici on veut récupérer les personnes depuis le tokenId)

      val newAlerts: List[AlertPersonne] =
        alertToken.map { alert =>
          val personne =
            personneRepository
              .findFromToken(alert.tokenId)
              //.getOrElse(// drop ?)

          val alertPersonne = aggregateWithPersonne(alert, personne)
          // Envoi de la notif

          //sendNotif(alertPersonne/*,httpClient*/)

          alertPersonne
        }

      alertRepository.saveAll(newAlerts)
    }
  }

  /*def sendNotif(alertPersonne : AlertPersonne/*, client: OkHttpClient*/): Unit = {
      // On récupère l'id personne
      val from_id = alertPersonne.user_id

      // On récupère la liste des autres membres de la famille (les parents)
      val idsParents :List[String] = personneRepository.findListIdFamily(from_id)
      (from_id)

      // Pour chaque parent on ajoute à AlertPersonne et on envoie
      for (parent <- idsParents) {
        val alertService = AlertService(
          to_id = parent,
          from_name = alertPersonne.user_name,
          from_id = alertPersonne.user_id,
          timestamp = alertPersonne.timestamp,
          server_timestamp = Instant.now().toEpochMilli,
          reason = alertPersonne.reason,
          coordinates = alertPersonne.coordinates
        )
        sendDoc(
                AlertServiceJson.serialize(alertService),
                s"http://adresse.com/alert/notify"/*,
                client*/
        )
      }

      

  }*/

  /*def sendDoc(doc: String, url: String, client: OkHttpClient): Unit = {
    println(s"Sending to $url: $doc")

    val body = RequestBody.create(doc, MediaType.parse("application/json"))
    val request = new Request.Builder()
      .url(url)
      .post(body)
      .build()

    Using(client.newCall(request).execute()) { response =>
      if (response.isSuccessful) {
        println(s"Success: data: $doc")
      } else {
        println(
          s"Error: ${response.message()} (${response.code()}) - data: $doc"
        )
      }
    }.get
  }*/

  def aggregateWithPersonne(
      alertToken: AlertIngestToken,
      personne: Personne
  ): AlertPersonne = {

    AlertPersonne(
        user_id = personne.idPersonne,
        user_name = personne.user_name,
        timestamp = alertToken.timestamp,
        reason = alertToken.reason,
        coordinates = alertToken.coordinates
      )
  }

  def nextAlertToken(consumer: KafkaConsumer[String, String]): List[AlertIngestToken] = {
    val records: Iterable[ConsumerRecord[String, String]] =
      consumer.poll(java.time.Duration.ofSeconds(5)).asScala

    records.map { record =>
      println(s"Got record: $record")
      val doc = record.value()
      AlertIngestTokenJson.deserialize(doc)
    }.toList
  }

}
