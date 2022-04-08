package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.util

class CassandraPersonneRepository(session: CqlSession)
    extends PersonneRepository {

  import scala.jdk.CollectionConverters._

  
  
  override def findIdFamily(idPersonne: String): String = {
    val statement =
      session.prepare("SELECT idFamily FROM tranquily.personne WHERE idPersonne = ?")
    val result: List[Row] =
      session.execute(statement.bind(idFamily)).all().asScala.toList

   result.map(result =>
        result.getString("idFamily")
      )
  }

  override def findFromToken(token: Long): Long = {
    val statement =
      session.prepare("SELECT * FROM tranquily.personne WHERE token = ?")
    val result: List[Row] =
      session.execute(statement.bind(idFamily)).all().asScala.toList

   result.map(result =>
      Personne(
        idPersonne = result.getString("idPersonne"),
        user_name = result.getString("user_name"),
        personne_type = result.getString("personne_type"),
        family_list = result.getList("family_list")
        idFamily = result.getString("idFamily")
      )
    )
  }


}
