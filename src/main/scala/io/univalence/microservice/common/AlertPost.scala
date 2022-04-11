package io.univalence.microservice.common

import com.google.gson.Gson

case class AlertPost(idEnfant: String, timestamp: Long, reason: String,user_name : String, coordinates : List[Float])
object AlertPostJson {
  val gson = new Gson()

  def serialize(alert: AlertPost): String =
    gson.toJson(alert)
  def deserialize(data: String): AlertPost =
    gson.fromJson(data, classOf[AlertPost])
}
