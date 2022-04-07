package io.univalence.microservice.common

object Configuration {

  val IngestHttpPort = 10001
  val ApiHttpPort    = 10000

  val TranquilyKeyspace = "tranquily"
  val MessageTable    = s"$StoreKeyspace.message"
  val AlertTable    = s"$StoreKeyspace.alert"
  val PersonneTable    = s"$StoreKeyspace.personne"

  val KafkaBootstrap = "localhost:9092"
  val StockInfoTopic = "stock-info"

  object StockTableFields {
    val id        = "id"
    val timestamp = "ts"
    val quantity  = "qtt"
  }

}
