package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait MessageRepository {

  def findAllByIdsFamily(start: Long, end: Long, count: Long, idsFamily: Array): Iterator[Message]

  def save(message: Message): Unit

}
