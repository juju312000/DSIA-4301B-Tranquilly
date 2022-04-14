package io.univalence.microservice.common

import com.google.gson.Gson

case class MessageIngestToken(token: String, timestamp: Long, message: String,coordinates : List[Double])
object MessageIngestTokenJson {
  val gson = new Gson()

  def serialize(alert: MessageIngestToken): String =
    gson.toJson(alert)
  def deserialize(data: String): MessageIngestToken =
    gson.fromJson(data, classOf[MessageIngestToken])
}
