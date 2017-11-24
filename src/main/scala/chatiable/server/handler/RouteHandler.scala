package chatiable.server.handler

import akka.http.scaladsl.server.Route

abstract class RouteHandler {
  def route: Route
}
