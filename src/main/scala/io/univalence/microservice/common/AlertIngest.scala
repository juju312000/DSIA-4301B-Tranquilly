package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertIngest(timestamp: Long, reason: String,coordinates : List[Double])
object AlertIngestJson {
  val gson = new Gson()

  def serialize(alert: AlertIngest): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertIngest =
    gson.fromJson(data, classOf[AlertIngest])
}
