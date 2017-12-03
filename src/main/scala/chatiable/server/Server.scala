package chatiable.server

import akka.http.scaladsl.server.Route

abstract class Server {
  def route: Route
}
