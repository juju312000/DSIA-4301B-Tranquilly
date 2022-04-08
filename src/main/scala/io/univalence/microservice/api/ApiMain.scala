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

        // Récupère ids des enfants
        val idsEnfants = Array

        val stocks: List[AlertGet] = alertRepository.findHistory(start,end,count,idsEnfants).toList
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

        // Récupère ids des enfants
        val idsEnfants = Array

        val stocks: List[AlertGet] = alertRepository.findHistory(start,end,count,idsEnfants).toList
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
