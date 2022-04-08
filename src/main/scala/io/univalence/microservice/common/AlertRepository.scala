package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait AlertRepository {

  def findHistory(start: Long, end: Long, count: Long, idEnfant: Array): Iterator[AlertPost]

  def save(projectedStock: ProjectedStock): Unit

  def findLastPosition(idEnfant: Long): Iterator[AlertPost]

  def saveAll(alert: List[AlertPost]): Unit

}
