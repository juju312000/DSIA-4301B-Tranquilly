package io.univalence.microservice.demo

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common.Configuration
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}

import scala.util.{Try, Using}

object InitMain {

  // Cassandra configuration
  val keyspace: String = Configuration.TranquillyKeyspace
  val messageTable: String    = Configuration.MessageTable
  val alertTable: String    = Configuration.AlertTable
  val personneTable: String    = Configuration.PersonneTable

  // Kafka configuration
  val topic: String   = Configuration.AlertTopic
  val partitions: Int = 8

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    println("--> Prepare Cassandra")

    // Cql done
    Using(CqlSession.builder().build()) { session =>
      println(s"Delete keyspace $keyspace...")

      Try(session.execute(s"DROP KEYSPACE $keyspace")).getOrElse(())

      println(s"Create keyspace family...")
      session.execute(s"""CREATE KEYSPACE IF NOT EXISTS $keyspace
            | WITH REPLICATION = {
            |   'class': 'SimpleStrategy',
            |   'replication_factor': 1
            | }""".stripMargin)

      println(s"Create table tranquilly.message ...")
      session.execute(s"""CREATE TABLE IF NOT EXISTS $messageTable (
            |  timestamp BIGINT,
            |  message TEXT,
            |  idPersonne TEXT,
            |  user_name TEXT,
            |  coordinates list<DOUBLE>,
            |
            |  PRIMARY KEY (idPersonne,timestamp)
            |)""".stripMargin)

      println(s"Create table tranquilly.alert ...")
      session.execute(s"""CREATE TABLE IF NOT EXISTS $alertTable (
            |  timestamp BIGINT,
            |  reason TEXT,
            |  idEnfant TEXT,
            |  user_name TEXT,
            |  coordinates list<DOUBLE>,
            |
            |  PRIMARY KEY (idEnfant,timestamp)
            |)""".stripMargin) 

      println(s"Create table tranquilly.personne ...")
      session.execute(s"""CREATE TABLE IF NOT EXISTS $personneTable (
            |  idPersonne TEXT,
            |  tokenId TEXT,
            |  typePersonne TEXT,
            |  user_name TEXT,
            |  idFamily TEXT,
            |  family_list list<TEXT>,
            |
            |  PRIMARY KEY (idPersonne)

            |)""".stripMargin)    
            // On pourait créer une table juste pour associer token et id

      println(s"Insert personne ...")
      session.execute(s"""INSERT INTO tranquilly.personne VALUES (1,Ey4Ip6JBGp,"parent","parent_1",1,[3,4])""".stripMargin)    
      session.execute(s"""INSERT INTO tranquilly.personne VALUES  (2,Ey4Ip6JBGm,"parent","parent_2",1,[3,4])""".stripMargin)    
      session.execute(s"""INSERT INTO tranquilly.personne VALUES  (3,Ey4Ip6JBGn,"enfant","enfant_1",1,[1,2])""".stripMargin)    
      session.execute(s"""INSERT INTO tranquilly.personne VALUES  (4,Ey4Ip6JBGh,"enfant","enfant_2",1,[1,2])""".stripMargin)    
    }.get

    println("--> Prepare Kafka")
    Using(
      AdminClient.create(
        Map[String, AnyRef](
          AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG -> Configuration.KafkaBootstrap
        ).asJava
      )
    ) { admin =>
      val nodes             = admin.describeCluster().nodes().get().asScala
      val replicationFactor = nodes.size.toShort
      println(s"Kafka cluster size: $replicationFactor")

      val topics = admin.listTopics().names().get().asScala

      if (topics.contains(topic)) {
        println(s"Topic $topic already exists")
        println(s"Delete topic $topic...")
        admin.deleteTopics(List(topic).asJava).all().get()
      }

      println(s"Creating topic $topic...")
      val newTopic = new NewTopic(topic, partitions, replicationFactor)
      admin.createTopics(List(newTopic).asJava).all().get()
    }
  }

}
