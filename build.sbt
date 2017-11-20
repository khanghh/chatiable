name := "chatiable"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-http_2.12" % Versions.AkkaHttp,
  "com.typesafe.akka" % "akka-stream_2.12" % Versions.AkkaStream,
  "com.typesafe.akka" % "akka-actor_2.12" % Versions.AkkaActor,
  "com.typesafe.slick" % "slick_2.12" % Versions.Slick,
  "com.typesafe.slick" % "slick-hikaricp_2.12" % Versions.Slick,
  "mysql" % "mysql-connector-java" % Versions.MySqlConnector,
)
