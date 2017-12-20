package chatiable.server.handler
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.service.chatfuel.ChatfuelApi
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class PrintParameterHandler extends RouteHandler {
  override def route: Route =
    path("params") {
      get {
        parameterMap { params =>
          def paramString(param: (String, String)): String = s"""${param._1} = '${param._2}'"""
          println(params.map(paramString).mkString("\n"))
          complete {
            StatusCodes.OK
          }
        }
      }
    }
}
