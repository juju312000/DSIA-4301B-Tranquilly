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
  override def findAllByIdsFamily(start: Long, end: Long, count: Long, idsFamily: List[Long]): Iterator[Message] = {
    val statement =
      session.prepare("SELECT * FROM tranquily.message WHERE timestamp BETWEEN(?,?) AND idFamily=  ? LIMIT ?")
    val result: List[Row] =
      session.execute(statement.bind(start,end,idsFamily,count)).all().asScala.toList

    result
      .map(result =>
        Message(
          idEnfant = result.getString("idPersonne"),
          reason = result.getString("reason"),
          timestamp = result.getLong("timestamp"),
          user_name = result.getInt("user_name"),
          coordinates = result.getInt("coordinates")
          // Comment ajouter server_timestamp ? Qu'est-ce ?
        )
      )
      .iterator
  }


  override def save(messageVar: Message): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquily.message(idPersonne, timestamp, message,user_name,coordinates) VALUES (?, ?, ?)")
    session.execute(
      statement.bind(
        messageVar.idPersonne,
        messageVar.timestamp,
        messageVar.message,
        messageVar.user_name,
        messageVar.coordinates
    )
  }

}
