package io.univalence.microservice.common

import com.google.gson.Gson

case class Alert(idEnfant: String, timestamp: Long, reason: String,user_name : String, coordinates : Array)
object AlertJson {
  val gson = new Gson()

  def serialize(alert: Alert): String =
    gson.toJson(alert)
  def deserialize(data: String): Alert =
    gson.fromJson(data, classOf[Alert])
}
