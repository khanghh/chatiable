package chatiable.server

object ChatiableConfig {
  val Namespace = "chatiable"
}

object MySqlConfig {
  val Namespace = s"${ChatiableConfig.Namespace}.mysql"
}