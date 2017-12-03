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
  "org.julienrf" %% "endpoints-algebra" % Versions.EndPoints,
  "org.julienrf" %% "endpoints-algebra-circe" % Versions.EndPoints,
  "org.julienrf" % "endpoints-akka-http-server_2.12" % Versions.EndPoints,
  "io.circe" % "circe-parser_2.12" % Versions.Circe,
  "io.circe" % "circe-generic_2.12" % Versions.Circe,
  "io.circe" % "circe-core_2.12" % Versions.Circe,
  "io.circe" % "circe-generic-extras_2.12" % Versions.Circe,
  "de.heikoseeberger" %% "akka-http-circe" % Versions.AkkaHttpCirce,
  "org.slf4j" % "slf4j-nop" % Versions.Slf4jNop,
)
