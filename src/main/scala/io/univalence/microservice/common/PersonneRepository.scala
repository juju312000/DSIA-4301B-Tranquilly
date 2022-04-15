package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait PersonneRepository {

  def findFromToken(token : String): Personne

  def findIdFamily(idPersonne: String): String

  def findListIdFamily(idFamily : String) : List[String]

}
