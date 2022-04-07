name := "microservice"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.apache.kafka"     % "kafka-clients"    % "3.1.0",
  "com.datastax.oss"     % "java-driver-core" % "4.14.0",
  "com.squareup.okhttp3" % "okhttp"           % "4.9.3",
  "com.sparkjava"        % "spark-core"       % "2.9.3",
  "com.google.code.gson" % "gson"             % "2.9.0",
  "org.slf4j"            % "slf4j-api"        % "1.7.32",
  "ch.qos.logback"       % "logback-classic"  % "1.2.10",
  "org.scalatest"       %% "scalatest"        % "3.2.11" % Test
)
