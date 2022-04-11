package io.univalence.microservice.common

import com.google.gson.Gson

case class MessagePersonne(user_id: String,user_name: String, timestamp: Long, message: String,coordinates : List[Float])
object MessagePersonneJson {
  val gson = new Gson()

  def serialize(alert: MessagePersonne): String =
    gson.toJson(alert)
  def deserialize(data: String): MessagePersonne =
    gson.fromJson(data, classOf[MessagePersonne])
}
