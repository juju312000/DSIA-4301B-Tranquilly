package io.univalence.microservice.common

import com.google.gson.Gson

case class PersonneId(idPersonne: String, user_name: String)
object PersonneIdJson {
  val gson = new Gson()

  def serialize(personne: Personne): String =
    gson.toJson(personne)
  def deserialize(data: String): Personne =
    gson.fromJson(data, classOf[Personne])
}
