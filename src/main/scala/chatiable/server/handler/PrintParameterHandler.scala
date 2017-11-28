package chatiable.server.handler
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.service.chatfuel.ChatfuelApi

class PrintParameterHandler extends RouteHandler {
  override def route: Route =
    path("params") {
      get {
        parameterMap { params =>
          def paramString(param: (String, String)): String = s"""${param._1} = '${param._2}'"""
          println(params.map(paramString).mkString("\n"))
          complete(ChatfuelApi.sendSilent)
        }
      }
    }
}
