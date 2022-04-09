package io.univalence.microservice.demo

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common.Configuration
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}

import scala.util.Using

object CleanMain {

  // Cassandra configuration
  val keyspace: String = Configuration.TranquillyKeyspace
  val MessageTable: String    = Configuration.MessageTable
  val AlertTable: String    = Configuration.AlertTable
  val PersonneTable: String    = Configuration.PersonneTable

  // Kafka configuration
  val topic: String   = Configuration.TranquillyInfoTopic

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    println("--> Clean Cassandra")
    Using(CqlSession.builder().build()) { session =>
      println(s"Delete keyspace $keyspace...")
      session.execute(s"DROP KEYSPACE $keyspace")
    }.fold(
      e => println(s"Error: ${e.getMessage}"),
      _ => ()
    )

    println("--> Clean Kafka")
    Using(
      AdminClient.create(
        Map[String, AnyRef](
          AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG -> Configuration.KafkaBootstrap
        ).asJava
      )
    ) { admin =>
      val topics = admin.listTopics().names().get().asScala

      if (topics.contains(topic)) {
        println(s"Topic $topic exists")
        println(s"Delete topic $topic...")
        admin.deleteTopics(List(topic).asJava).all().get()
      } else {
        println(s"Topic $topic does not exist. Do nothing")
      }
    }
  }

}
