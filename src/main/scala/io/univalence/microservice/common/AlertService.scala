package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertService(to_id: String,to_name: String, from_id: String, from_name: String, timestamp: Long, reason: String,user_name : String, coordinates : List[Double],server_timestamp : Long)
object AlertServiceJson {
  val gson = new Gson()

  def serialize(alert: AlertService): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertService =
    gson.fromJson(data, classOf[AlertService])
}
