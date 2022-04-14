package io.univalence.microservice.demo

import io.univalence.microservice.common.{
  Configuration,
  Message,
  Personne,
  PersonneId,
  AlertGet,
  AlertPersonne,
}
import okhttp3._

import scala.util.{Random, Using}

/** Inject random cord data in the application.
  */
object InjectorMain {

  /** Internal stock state to ensure that each stock quantity will not be less than zero.
    */
  val stockDb: scala.collection.mutable.Map[String, Int] =
    scala.collection.mutable.Map.empty

  def main(args: Array[String]): Unit = {
    val httpClient = new OkHttpClient.Builder().build()

    println("--> Initialize table")

    initStocks(httpClient)

    println("--> Send coord for tracking")

    /** Endlessly sends data...
      */
    while (true) {

      /** Ensure to have an average of stock update 30% of the time, and delta 70% of the time.
        */
      if (Random.nextInt(100) < 30) {
        val stock = nextStock

        println(s"Serializing: $stock")
        sendStock(stock.id, StockJson.serialize(stock), httpClient)
      } else {
        val delta = nextDeltaStock

        // ensure that stock for each prduct is positive
        if (stockDb(delta.id) + delta.delta >= 0) {
          println(s"Serializing: $delta")
          sendDelta(delta.id, DeltaStockJson.serialize(delta), httpClient)
          // update stock value
          stockDb(delta.id) = stockDb(delta.id) + delta.delta
        } else {
          println(s"Not enough stock for product#${delta.id}")
        }
      }

      pause()
    }
  }

  /** Initialize the whole application with a basis stock value.
    */
  def initStocks(httpClient: OkHttpClient): Unit = {
    for (id <- Stock.transco.keys) {
      val stock = createStock(id)
      stockDb(id) = stock.quantity

      println(s"Observed stock for product#$id: ${stock.quantity}")

      sendStock(id, StockJson.serialize(stock), httpClient)

      pause()
    }
  }

  /** Some random pause...
    */
  def pause(): Unit = {
    val waitTime = Random.nextInt(1000) + 1000
    Thread.sleep(waitTime)
  }

  def sendDelta(id: String, doc: String, client: OkHttpClient): Unit =
    sendDoc(
      doc,
      s"http://localhost:${Configuration.IngestHttpPort}/deltas/$id",
      client
    )

  def sendStock(id: String, doc: String, client: OkHttpClient): Unit =
    sendDoc(
      doc,
      s"http://localhost:${Configuration.IngestHttpPort}/stocks/$id",
      client
    )

  def sendDoc(doc: String, url: String, client: OkHttpClient): Unit = {
    println(s"Sending to $url: $doc")

    val body = RequestBody.create(doc, MediaType.parse("application/json"))
    val request = new Request.Builder()
      .url(url)
      .post(body)
      .build()

    Using(client.newCall(request).execute()) { response =>
      if (response.isSuccessful) {
        println(s"Success: data: $doc")
      } else {
        println(
          s"Error: ${response.message()} (${response.code()}) - data: $doc"
        )
      }
    }.get
  }

  def createStock(id: String): Stock =
    Stock(id = id, quantity = Random.nextInt(400) + 100)

  def createDeltaStock(id: String): DeltaStock =
    DeltaStock(id = id, delta = Random.nextInt(10) - 5)

  def nextStock: Stock = {
    val id = Random.shuffle(Stock.transco.keys.toList).head

    val stock = createStock(id)

    println(s"Observed stock: ${stock.quantity} for product#$id")

    stock
  }

  def nextDeltaStock: DeltaStock = {
    val id         = Random.shuffle(Stock.transco.keys.toList).head
    val deltaStock = createDeltaStock(id)

    if (deltaStock.delta < 0)
      println(s"Someone want to buy ${-deltaStock.delta} of product#$id")
    else
      println(s"Someone deliver ${deltaStock.delta} of product#$id")

    deltaStock
  }

}
