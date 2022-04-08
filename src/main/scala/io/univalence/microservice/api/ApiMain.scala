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

    get(
      "/api/history/alert",
      (request: Request, response: Response) => {
        println(s"--> Requested to find alert")
        val start = request.params("start")
        val stop = request.params("stop")
        val count = request.params("count")

        // Récupère le token
        val token : request.headers("Authorization")// à modifier si non fonctionnel 

        // Récupère ids des enfants
        val idsEnfants = Long

<<<<<<< Updated upstream
        val stocks: List[AlertGet] = AlertRepository.findHistory(start,end,count,idsEnfants).toList
=======
        val stocks: List[AlertGet] = alertRepository.findHistory(start,stop,count,idsEnfants).toList
>>>>>>> Stashed changes
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

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
        val token : request.headers("Authorization")// à modifier si non fonctionnel

        // Récupère ids des enfants
        val idsEnfants = Array

        val stocks: List[AlertGet] = MessageRepository.findHistory(start,end,count,idsEnfants).toList
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

        response.`type`("application/json")
        doc
      }
    )






    get(
      "/api/track:user_id",
      (request: Request, response: Response) => {
        println(s"--> Requested to find alert")
        val user_id : Long = request.params("user_id").toLong

        // Récupère le token
        //Find tolen

<<<<<<< Updated upstream
        // Récupère ids des enfants
        val idsEnfants = Array

        val stocks: List[AlertGet] = alertRepository.findLastPosition(start,end,count,idsEnfants).toList
=======
        val stocks: List[AlertGet] = alertRepository.findLastPosition(user_id).toList
>>>>>>> Stashed changes
        val doc                          = AlertGetJson.gson.toJson(stocks.asJava)

        response.`type`("application/json")
        doc

      }
    )

    awaitStop()
  }

}
