package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait PersonneRepository {

  def findFromToken(token : String): Option[Personne]

  def findFromId(idPersonne : String): Option[Personne]
  
  def findParents(idPersonne: String): Iterator[Personne]

  def findChildren(idPersonne: String): Iterator[Personne]


}
