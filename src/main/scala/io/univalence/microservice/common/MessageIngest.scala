package io.univalence.microservice.common

import com.google.gson.Gson

case class MessageIngest(timestamp: Long, reason: String,message: String, coordinates : List[Double])
object MessageIngestJson {
  val gson = new Gson()

  def serialize(alert: MessageIngest): String =
    gson.toJson(alert)
  def deserialize(data: String): MessageIngest =
    gson.fromJson(data, classOf[MessageIngest])
}
