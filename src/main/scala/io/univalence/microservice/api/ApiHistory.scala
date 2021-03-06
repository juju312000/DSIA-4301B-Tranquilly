package io.univalence.microservice.api

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import spark.Spark._
import spark.{Request, Response}

object ApiHistory {

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
        val start : Long = request.params("start").toLong
        val stop : Long= request.params("stop").toLong
        val count : Int = request.params("count").toInt

        // Récupère le tokenId
        //val tokenId : request.headers("Authorization")// à modifier si non fonctionnel
        //personneRepository.findFromToken()

        // Récupère ids des enfants
        //val personne = personneRepository.findFromToken(tokenId)
        //val enfants = personne.family_list
        val idsEnfants : Long = 2

        val messages: List[Message] = messageRepository.findAllMessagesByIdFamily(start,stop,count,idsEnfants).toList
        val doc                          = MessageJson.gson.toJson(messages.asJava)

        response.`type`("application/json")
        doc
      }
    )

    // à modifier
    get(
      "/api/history/alert",
      (request: Request, response: Response) => {
        println(s"--> Requested to find message")
        
        // Récupère le tokenId
        //val tokenId : request.headers("Authorization")// à modifier si non fonctionnel
        //personneRepository.findFromToken()

        // Récupère ids des enfants
        val start : Long = request.params("start").toLong
        val stop : Long= request.params("stop").toLong
        val count : Int = request.params("count").toInt

        val tokenId : String = null
        val personne = personneRepository.findFromToken(tokenId)
        val enfants = personne.family_list

        val alerts: List[AlertGet] = alertRepository.findHistory(start,stop,count,enfants).toList
        val doc                          = AlertGetJson.gson.toJson(alerts.asJava)

        response.`type`("application/json")
        doc
      }
    )

    awaitStop()
  }

}
