package io.univalence.microservice.common

/**
 * A repository is a usual design pattern that add a layer to decouple
 */
trait MessageRepository {

  def findAllMessagesByIdFamily(start: Long, end: Long, count: Long, idsFamily: Long): Iterator[Message]

  def save(message: Message): Unit

  def saveAll(messages: List[Message]): Unit

}
