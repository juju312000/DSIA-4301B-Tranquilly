package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.util

class CassandraAlertRepository(session: CqlSession)
    extends AlertRepository {

  import scala.jdk.CollectionConverters._

  
  // Pas sur du type Array
  // Prend les ids des enfant, les dates limites et le nombre maximum à récupérer
  // Appel depuis history/alert
  override def findHistory(start: Long, end: Long, count: Long, idsEnfants: Array): Iterator[Alert] = {
    val statement =
      session.prepare("SELECT * FROM tranquily.alert WHERE reason in ('ZONEOUT','REDBUTTON','PHONEOFF') AND timestamp BETWEEN(?,?) AND idEnfant in ? LIMIT ?")
    val result: List[Row] =
      session.execute(statement.bind(start,end,idsEnfants,count)).all().asScala.toList

    result
      .map(result =>
        AlertHistory(
          idEnfant = result.getString("idEnfant"),
          reason = result.getString("reason"),
          timestamp = result.getLong("timestamp"),
          user_name = result.getInt("user_name"),
          coordinates = result.getInt("coordinates")
          // Comment ajouter server_timestamp ? Qu'est-ce ?

          
        )
      )
      .iterator
  }


  override def save(alert: AlertPost): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquily.alert(idEnfant, timestamp, reason,user_name,coordinates) VALUES (?, ?, ?, ?, ?)")
    session.execute(
      statement.bind(
        alert.idEnfant,
        alert.timestamp,
        alert.reason,
        alert.user_name,
        alert.coordinates
    )
  }

// Pas sur du type Array
  // Appel depuis TRACK API
  override def findPosition(idEnfant: Array): Iterator[AlertGet] = {
    val statement =
      session.prepare("SELECT * FROM tranquily.alert WHERE reason = 'TRACKING' AND idEnfant == ? ORDER BY timestamp DESC LIMIT 1")
      // Permet de récupérer la dernière position d'un enfant
    val result: List[Row] =
      session.execute(statement.bind(idEnfant)).all().asScala.toList

    result
      .map(result =>
        AlertGet(
          idEnfant = result.getString("idEnfant"),
          timestamp = result.getLong("ts"),
          user_name = result.getInt("user_name"),
          coordinates = result.getInt("coordinates"),
          server_timestamp = Instant.now().toEpochMilli
        )
      )
      .iterator
  }


  override def saveAll(alert: List[AlertPost]): Unit = {
    val statement =
      session.prepare("SELECT * FROM tranquily.alert WHERE reason in ('ZONEOUT','REDBUTTON','PHONEOFF') AND timestamp BETWEEN(?,?) AND idEnfant in ? LIMIT ?")

    val batch =
      BatchStatement
        .newInstance(BatchType.LOGGED)
        .addAll(
          stocks
            .map(stock =>
              statement.bind(
                      alert.idEnfant,
                      alert.timestamp,
                      alert.reason,
                      alert.user_name,
                      alert.coordinates
                ))
            .asJava
        )

    session.execute(batch)
  }


}
