package io.univalence.microservice.common

object Configuration {

  val IngestHttpPort = 10001
  val ApiHttpPort    = 10000

  val TranquillyKeyspace = "tranquilly"
  val MessageTable    = s"$TranquillyKeyspace.message"
  val AlertTable    = s"$TranquillyKeyspace.alert"
  val PersonneTable    = s"$TranquillyKeyspace.personne"

  val KafkaBootstrap = "localhost:9092"
  val TranquillyInfoTopic = "tranquilly-info"

  object StockTableFields {
    val id        = "id"
    val timestamp = "ts"
    val quantity  = "qtt"
  }

}
