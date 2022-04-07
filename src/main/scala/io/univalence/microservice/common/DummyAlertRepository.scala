package io.univalence.microservice.common

class DummyAlertRepository extends AlertRepository {

  override def findAll(): Iterator[Alert] = Iterator.empty

  override def findById(id: String): Option[Alert] = None

  override def save(Alert: Alert): Unit = ()

  override def saveAll(stocks: List[Alert]): Unit = ()

}


// A quoi ca sert ?