package io.univalence.microservice.common

object Configuration {

  val IngestMessageHttpPort = 10002
  val IngestAlertHttpPort = 10001
  val ApiHttpPort    = 10000

  val TranquilyKeyspace = "tranquily"
  val MessageTable    = s"$StoreKeyspace.message"
  val AlertTable    = s"$StoreKeyspace.alert"
  val PersonneTable    = s"$StoreKeyspace.personne"

  val KafkaBootstrap = "localhost:9092"
  val AlertTopic = "alert"
  val MessageTopic = "message"

  object StockTableFields {
    val id        = "id"
    val timestamp = "ts"
    val quantity  = "qtt"
  }

}
