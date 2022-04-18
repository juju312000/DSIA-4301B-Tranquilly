package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait AlertRepository {

  def findHistory(start: Long, end: Long, count: Int, idsEnfants: List[String]): Iterator[AlertGet]

  def save(alert: AlertPersonne): Unit

  def findLastPosition(idEnfant: String): Option[AlertPersonne]

  def saveAll(alert: List[AlertPersonne]): Unit

}
