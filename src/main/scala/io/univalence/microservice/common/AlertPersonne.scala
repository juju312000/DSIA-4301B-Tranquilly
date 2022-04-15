package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertPersonne(user_id: String,user_name: String, timestamp: Long, reason: String,coordinates : List[Double])
object AlertPersonneJson {
  val gson = new Gson()

  def serialize(alert: AlertPersonne): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertPersonne =
    gson.fromJson(data, classOf[AlertPersonne])
}
