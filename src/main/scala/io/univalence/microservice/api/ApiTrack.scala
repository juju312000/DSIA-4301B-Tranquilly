package io.univalence.microservice.api

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import spark.Spark._
import spark.{Request, Response}

object ApiTrack {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    port(Configuration.ApiHttpPort)
    println(s"Ready on http://localhost:${Configuration.ApiHttpPort}/")

    // TODO instantiate a stock repository from a Cassandra session

    val session = CqlSession.builder().build()

    val alertRepository: AlertRepository = new CassandraAlertRepository(session)
    val personneRepository: PersonneRepository = new CassandraPersonneRepository(session)

    get(
      "/api/track",
      (request: Request, response: Response) => {
        println(s"--> Requested to find alert")
        
        // Récupère le tokenId
        //val tokenId : request.headers("Authorization")// à modifier si non fonctionnel
        //personneRepository.findFromToken()

        // Récupère ids des enfants
        //val personne =personneRepository.findFromToken(tokenId)
        //val enfants = personne.family_list

        //  request.headers("Authorization")// à modifier si non fonctionnel
        val tokenId : String = null
        val parent : Personne =  personneRepository.findFromToken(tokenId)
        val idEnfant : String = request.params("id").toString

        if (parent.family_list.contains(idEnfant)) {
          println(s"Personne $idEnfant not in child List")
          response.status(404)
          s"Personne #$idEnfant not in child List"
        } else {
          val track: AlertPersonne = alertRepository.findLastPosition(idEnfant)
          val doc                         = AlertGetJson.gson.toJson(track)
          response.`type`("application/json")
          doc
        }
      }
    )

    awaitStop()
  }

}
