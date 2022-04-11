package io.univalence.microservice.common

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatement, BatchType, Row}

import java.util

class CassandraPersonneRepository(session: CqlSession)
    extends PersonneRepository {

  import scala.jdk.CollectionConverters._

  
  
  override def findIdFamily(idPersonne: String): Long = {
    val statement =
      session.prepare("SELECT idFamily FROM tranquily.personne WHERE idPersonne = ?")

    val result: Option[Row] = Option(session.execute(statement.bind(idFamily)).one())


   result.map(result =>
        result.getString("idFamily")
      )
  }

  override def findFromToken(token: String): Personne = {
    val statement =
      session.prepare("SELECT * FROM tranquily.personne WHERE token = ?")
    val result: Option[Row] = Option(session.execute(statement.bind(token)).one())
    
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
