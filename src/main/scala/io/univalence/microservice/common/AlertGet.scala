package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertGet(idEnfant: String, timestamp: Long, reason: String,user_name : String, coordinates : List[Float],server_timestamp : Long)
object AlertGetJson {
  val gson = new Gson()

  def serialize(alert: AlertGet): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertGet =
    gson.fromJson(data, classOf[AlertGet])
}
