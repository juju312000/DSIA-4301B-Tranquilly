package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.time.Instant
import java.util


class CassandraAlertRepository(session: CqlSession)
    extends AlertRepository {

    import scala.jdk.CollectionConverters._
  
  // Pas sur du type Array
  // Prend les ids des enfant, les dates limites et le nombre maximum à récupérer
  // Appel depuis history/alert
  override def findHistory(start: Long, end: Long, count: Int, idsEnfants: List[String]): Iterator[AlertGet] = {
    val statement =
      session.prepare("SELECT * FROM tranquilly.alert WHERE reason in ('ZONEOUT','REDBUTTON','PHONEOFF') AND timestamp BETWEEN(?,?) AND idEnfant in ? LIMIT ?")
    val result: List[Row] =
      session.execute(statement.bind(start,end,idsEnfants,count)).all().asScala.toList

    result
      .map(result =>
        AlertGet(
          idEnfant = result.getString("idEnfant"),
          reason = result.getString("reason"),
          timestamp = result.getLong("timestamp"),
          user_name = result.getString("user_name"),
          coordinates = result.getList("coordinates", classOf[Double]).asScala.toList,
          server_timestamp = Instant.now().toEpochMilli
        )
      ).iterator
  }

  override def save(alert: AlertPersonne): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquilly.alert(idEnfant, timestamp, reason,user_name,coordinates) VALUES (?, ?, ?, ?, ?)")
    session.execute(
      statement.bind(
        alert.user_id,
        alert.user_name,
        alert.timestamp,
        alert.reason,
        alert.coordinates
      ))
  }

// Pas sûr du type Array
  // Appel depuis TRACK API
  override def findLastPosition(idEnfant: String): Option[AlertPersonne] = {
    val statement =
      session.prepare("SELECT * FROM tranquilly.alert WHERE reason = 'TRACKING' AND idEnfant == ? ORDER BY timestamp DESC LIMIT 1")
      // Permet de récupérer la dernière position d'un enfant
    val result: Option[Row] =
      Option(session.execute(statement.bind(idEnfant)).one())

    result
      .map(result =>
        AlertPersonne(
          user_id = result.getString("user_id"),
          user_name = result.getString("user_name"),
          reason = result.getString("reason"),
          timestamp = result.getLong("ts"),
          coordinates = result.getList("coordinates", classOf[Double]).asScala.toList,
        )
      ).get
  }

  override def saveAll(alert: List[AlertPersonne]): Unit = {
    val statement =
      session.prepare("INSERT INTO tranquily.alert(idEnfant, timestamp, reason,user_name,coordinates) VALUES (?, ?, ?, ?, ?)")

    val batch =
      BatchStatement
        .newInstance(BatchType.LOGGED)
        .addAll(
          alert
            .map(alert =>
              statement.bind(alert.user_id,alert.reason,alert.timestamp,alert.user_name,alert.coordinates)
            )
            .asJava
        )
    session.execute(batch)
  }
}
