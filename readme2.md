# Microservice

We will an application that manages stocks data coming from a store.

Stock data are two-folds. There are :
* _observed stock_ indicating the new quantity of a product in the
  store.
* _delta_ indicating a variation in the stock (due to a sale or
  delivery).

## Goal

To build an application that indicates the current value of each stock.

Constraints:
* The application updates its data according to the received
  _observed stocks_ and _delta_.
* The application is scalable
* The application is still available even in case of partial failure.

## Run it!

### Install Kafka & Cassandra

* [Apache Kafka](https://kafka.apache.org/)
* [Apache Cassandra](https://cassandra.apache.org/_/index.html)

Instructions
* Install Kafka (download the TGZ file and uncompress it)
* Launch Zookeeper:
```shell
$ ./bin/zookeeper-server-start.sh config/zookeeper.properties
```

Windows:
```shell
> ./bin/windows/zookeeper-server-start.bat config/zookeeper.properties
```

* Launch Kafka:
```shell
$ ./bin/kafka-server-start.sh config/server.properties
```
And a second instance
```shell
$ ./bin/kafka-server-start.sh config/server.properties \
  --override broker.id=1 \
  --override log.dirs=/tmp/kafka-logs.1 \
  --override listeners=PLAINTEXT://:9093
```

Windows:
```shell
> ./bin/windows/kafka-server-start.sh config/server.properties
```

* Launch Cassandra (with Docker)
```shell
$ docker run -p 9042:9042 --name cassandra-db --rm cassandra:4.0.3
```

### Prepare Kafka & Cassandra

Simply run `InitMain` in the `demo` package with IntelliJ IDEA.

### Run the applications

With IntelliJ IDEA

1. Run `api.ApiMain`
1. Run `process.ProcessMain`
1. Run `ingest.IngestMain`

### Demo

On the ingest side, run `demo.InjectorMain`.

On the API side, try one of thoses URLs:

* http://localhost:10000/api/stocks/1
* http://localhost:10000/api/stocks/2
* http://localhost:10000/api/stocks/unknown
* http://localhost:10000/api/stocks

## Architecture

The architecture below shows the different parts of our application.
Each item between brackets (`[ABC]`) represents a microservice, which a
scalable unit of work. Each item between braces (`(Abc)`) represents
communication or a storage system.

Note: demo parts are here to simulate the behavior of an external user.

```
[demo/Injector]
    |
    | (HTTP)
    V
[INGEST]
    |
    | (Kafka)
    V
[PROCESS]
    |
    V
(Cassandra)
    |
    V
  [API]
    |
    | (HTTP)
    V
[demo/Reader]
```

## Reminder

* Always rely on memory model to process data, and not on whatever
  representation (String, JSON, binary...). Use those kinds of
  representation only for communication and storage, not for data
  processing.
