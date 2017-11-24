package chatiable.server.handler
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class PrintParameterHandler extends RouteHandler {
  override def route: Route =
    path("params") {
      get {
        parameterMap { params =>
          def paramString(param: (String, String)): String = s"""${param._1} = '${param._2}'"""
          println(params.map(paramString).mkString("\n"))
          complete(s"The parameters are ${params.map(paramString).mkString(", ")}")
        }
      }
    }
}
