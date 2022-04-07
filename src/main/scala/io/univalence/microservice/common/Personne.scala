package io.univalence.microservice.common

import com.google.gson.Gson

case class Personne(idPersonne: String, user_name: String, parent_list: List,children_list : String, personne_type : String)
object PersonneJson {
  val gson = new Gson()

  def serialize(personne: Personne): String =
    gson.toJson(personne)
  def deserialize(data: String): Personne =
    gson.fromJson(data, classOf[Personne])
}
