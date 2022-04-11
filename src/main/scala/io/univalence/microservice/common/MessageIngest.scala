package io.univalence.microservice.common

import com.google.gson.Gson

case class Message(idpersonne: String, timestamp: Long, message: String,user_name : String, coordinates : Array)
object MessageJson {
  val gson = new Gson()

  def serialize(message: Message): String =
    gson.toJson(message)
  def deserialize(data: String): Message =
    gson.fromJson(data, classOf[Message])
}