package io.univalence.microservice.api

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import spark.Spark._
import spark.{Request, Response}

object ApiMainHistoryMessage {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    port(Configuration.ApiHttpPort)
    println(s"Ready on http://localhost:${Configuration.ApiHttpPort}/")

    // TODO instantiate a stock repository from a Cassandra session

    val session = CqlSession.builder().build()

    val alertRepository: AlertRepository = new CassandraAlertRepository(session)
    val messageRepository: MessageRepository = new CassandraMessageRepository(session)
    val personneRepository: PersonneRepository = new CassandraPersonneRepository(session)

    // à modifier
    get(
      "/api/history/message",
      (request: Request, response: Response) => {
        println(s"--> Requested to find message")
        val start = request.params("start")
        val stop = request.params("stop")
        val count = request.params("count")

        // Récupère le token
        //val token : request.headers("Authorization")// à modifier si non fonctionnel
        //personneRepository.findFromToken()

        // Récupère ids des enfants
        //val personne = personneRepository.findFromToken(token)
        //val enfants = personne.family_list

        val stocks: List[AlertGet] = messageRepository.findAllMessagesByIdsFamily(start,stop,count,idsEnfants).toList
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

        response.`type`("application/json")
        doc
      }
    )

    awaitStop()
  }

}
