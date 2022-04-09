package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.util

class CassandraPersonneRepository(session: CqlSession)
    extends PersonneRepository {

  import scala.jdk.CollectionConverters._

  
  
  override def findIdFamily(idPersonne: Long): List[Long] = {
    val statement =
      session.prepare("SELECT idFamily FROM tranquilly.personne WHERE idPersonne = ?")
    val result: List[Row] =
      session.execute(statement.bind(idPersonne)).all().asScala.toList

   result
     .map(
       result =>
        result.getLong("idFamily")
      )
  }

  override def findFromToken(token: Long): List[Personne] = {
    val statement =
      session.prepare("SELECT * FROM tranquilly.personne WHERE token = ?")
    val result: List[Row] =
      session.execute(statement.bind(token)).all().asScala.toList

   result.map(result =>
      Personne(
        idPersonne = result.getLong("idPersonne"),
        user_name = result.getString("user_name"),
        personne_type = result.getString("personne_type"),
        idFamily = result.getLong("idFamily"),
        family_list = result.getList("family_list", classOf[Long]).asScala.toList
      )
    )
  }


}
