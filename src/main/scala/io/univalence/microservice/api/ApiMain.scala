package io.univalence.microservice.api

import com.datastax.oss.driver.api.core.CqlSession
import io.univalence.microservice.common._
import spark.Spark._
import spark.{Request, Response}

object ApiMain {

  import scala.jdk.CollectionConverters._

  def main(args: Array[String]): Unit = {
    port(Configuration.ApiHttpPort)
    println(s"Ready on http://localhost:${Configuration.ApiHttpPort}/")

    // TODO instantiate a stock repository from a Cassandra session

    val session = CqlSession.builder().build()

    val alertRepository: AlertRepository = new CassandraAlertRepository(session)
    val messageRepository: MessageRepository = new CassandraMessageRepository(session)
    val personneRepository: PersonneRepository = new CassandraPersonneRepository(session)


    get(
      "/api/history/alert",
      (request: Request, response: Response) => {
        println(s"--> Requested to find alert")
        val start = request.params("start").toLong
        val stop = request.params("stop")
        val count = request.params("count")

        // Récupère le token
        val token : request.headers.get("Authorization")// à modifier si non fonctionnel
        personneRepository.findFromToken()

        // Récupère ids des enfants
        val personne = personneRepository.findFromToken(token)
        val idEnfants = personne.family_list

        val alerts: List[AlertGet] = alertRepository.findHistory(start,stop,count,idEnfants).toList
        val doc                          = AlertGetJson.gson.toJson(alerts.asJava)

        response.`type`("application/json")
        doc
      }
    )


    // à modifier
    get(
      "/api/history/message",
      (request: Request, response: Response) => {
        println(s"--> Requested to find message")
        val start = request.params("start")
        val stop = request.params("stop")
        val count = request.params("count")

        // Récupère le token
        val token : request.headers.get("Authorization")// à modifier si non fonctionnel
        personneRepository.findFromToken()

        // Récupère ids des enfants
        val personne = personneRepository.findFromToken(token)
        val enfants = personne.family_list

        val stocks: List[AlertGet] = messageRepository.findHistory(start,stop,count,idsEnfants).toList
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

        response.`type`("application/json")
        doc
      }
    )


    get(
      "/api/track",
      (request: Request, response: Response) => {
        println(s"--> Requested to find alert")
        val start = request.params("start")
        val stop = request.params("stop")
        val count = request.params("count")

        // Récupère le token
        val token : request.headers.get("Authorization")// à modifier si non fonctionnel
        personneRepository.findFromToken()

        // Récupère ids des enfants
        val personne =personneRepository.findFromToken(token)
        val enfants = personne.family_list

        val stocks: List[AlertGet] = alertRepository.findLastPosition(start,end,count,enfants).toList
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

        response.`type`("application/json")
        doc
      }
    )

    get(
      "/api/stocks/:id",
      (request: Request, response: Response) => {
        val id = request.params("id")
        println(s"--> Requested to find stock of #$id")
        val stock: Option[ProjectedStock] = repository.findById(id)

        if (stock.isEmpty) {
          println(s"product#$id not found")
          response.status(404)

          s"Product#$id Not Found"
        } else {
          println(s"product#$id: $stock")
          val doc = ProjectedStockJson.serialize(stock.get)

          response.`type`("application/json")
          doc
        }
      }
    )

    awaitStop()
  }

}
