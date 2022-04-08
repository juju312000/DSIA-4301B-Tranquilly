package io.univalence.microservice.common

class DummyAlertRepository extends AlertRepository {

  override def findAll(): Iterator[AlertPost] = Iterator.empty

  override def findById(id: String): Option[AlertPost] = None

  override def save(alert: AlertPost): Unit = ()

  override def saveAll(stocks: List[AlertPost]): Unit = ()

}


// A quoi ca sert ?