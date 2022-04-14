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
        
        // Récupère le token
        //val token : request.headers("Authorization")// à modifier si non fonctionnel
        //personneRepository.findFromToken()

        // Récupère ids des enfants
        //val personne =personneRepository.findFromToken(token)
        //val enfants = personne.family_list

        //  request.headers("Authorization")// à modifier si non fonctionnel
        val token : String = NULL
        val parent : Personne =  personneRepository.findFromToken(token)
        val idEnfant : String = request.params("id").toString

        if (parent.listFamily contains idEnfant) {
          println(s"Personne #$id not in child List")
          response.status(404)
          s"Personne #$id not in child List"
        } else {
          val stocks: AlertGet = alertRepository.findLastPosition(idEnfant)
          val doc                         = AlertGetJson.gson.toJson(stocks.asJava)
          response.`type`("application/json")
          doc
        }
      }
    )

    awaitStop()
  }

}
