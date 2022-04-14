osascript -e 'tell app "Terminal"
    do script "~/opt/kafka_2.13-3.1.0/bin/zookeeper-server-start.sh ~/opt/kafka_2.13-3.1.0/config/zookeeper.properties"
end tell'

osascript -e 'tell app "Terminal"
    do script "~/opt/kafka_2.13-3.1.0/bin/kafka-server-start.sh ~/opt/kafka_2.13-3.1.0/config/server.properties"
end tell'

osascript -e 'tell app "Terminal"
    do script "~/opt/kafka_2.13-3.1.0/bin/kafka-server-start.sh ~/opt/kafka_2.13-3.1.0/config/server.properties --override broker.id=1 --override log.dirs=/tmp/kafka-logs.1 --override listeners=PLAINTEXT://:9093"
end tell'

docker run -p 9042:9042 --name cassandra-db --rm cassandra:4.0.3