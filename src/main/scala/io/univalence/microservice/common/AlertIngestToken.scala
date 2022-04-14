package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertIngestToken(token: String, timestamp: Long, reason: String,coordinates : List[Double])
object AlertIngestTokenJson {
  val gson = new Gson()

  def serialize(alert: AlertIngestToken): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertIngestToken =
    gson.fromJson(data, classOf[AlertIngestToken])
}
