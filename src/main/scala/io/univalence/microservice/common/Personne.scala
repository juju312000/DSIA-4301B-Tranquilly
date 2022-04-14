package io.univalence.microservice.common

import com.google.gson.Gson

case class Personne(idPersonne: String, user_name: String, personne_type : String, idFamily : String, family_list: List[String])
object PersonneJson {
  val gson = new Gson()

  def serialize(personne: Personne): String =
    gson.toJson(personne)
  def deserialize(data: String): Personne =
    gson.fromJson(data, classOf[Personne])
}
