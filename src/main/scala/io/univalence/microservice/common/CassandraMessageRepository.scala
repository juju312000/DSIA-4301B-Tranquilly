package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.util

class CassandraMessageRepository(session: CqlSession)
    extends MessageRepository {

  import scala.jdk.CollectionConverters._

  
  // Pas sur du type Array
  // Prend les ids des enfants, les dates limites et le nombre maximum à récupérer
  // Appel depuis history/Message

  // HISTORY API
  override def findAllMessagesByIdsFamily(start: Long, end: Long, count: Long, idsFamily: Long): Iterator[Message] = {
    val statement =
      session.prepare("SELECT * FROM tranquily.message WHERE timestamp BETWEEN(?,?) AND idFamily=  ? LIMIT ?")
    val result: List[Row] =
      session.execute(statement.bind(start,end,idsFamily,count)).all().asScala.toList

    result
      .map(result =>
        Message(
          idPersonne = result.getString("idPersonne"),
          message = result.getString("reason"),
          timestamp = result.getLong("timestamp"),
          user_name = result.getString("user_name"),
          coordinates = result.getList("coordinates", classOf[Double]).asScala.toList
          // Comment ajouter server_timestamp ? Qu'est-ce ?
        )
      )
      .iterator
  }

  // MESSAGE API
  override def save(messageVar: Message): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquilly.message(idPersonne, timestamp, message,user_name,coordinates) VALUES (?, ?, ?, ?, ?)")
    session.execute(
      statement.bind(
        messageVar.idPersonne,
        messageVar.timestamp,
        messageVar.message,
        messageVar.user_name,
        messageVar.coordinates
    ))
  }

  //save all message in cassandra direct
  override def saveAll(messages: List[Message]): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquilly.message(idPersonne, timestamp, message,user_name,coordinates) VALUES (?, ?, ?, ?,?)")

    val batch =
      BatchStatement
        .newInstance(BatchType.LOGGED)
        .addAll(
          messages
            .map(message =>
              statement.bind(message.idPersonne,message.timestamp,message.message,message.user_name,message.coordinates ))
            .asJava
        )
    session.execute(batch)
  }
}
