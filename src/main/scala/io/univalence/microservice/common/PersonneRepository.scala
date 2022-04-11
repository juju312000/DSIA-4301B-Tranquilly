package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait PersonneRepository {

  def findFromToken(token : String): Option[Personne]

  def findIdFamily(idPersonne: String): String


}
