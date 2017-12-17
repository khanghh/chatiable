package chatiable.server.facebook

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import chatiable.server.Server
import chatiable.service.facebook.message.FBWebhooksService

final class WebhooksServer(
  fbWebhooksService: FBWebhooksService
) extends Server {
  override def route: Route =
    path("webhooks") {
      get {
        parameterMap { params =>
          def paramString(param: (String, String)): String = s"""${param._1} = '${param._2}'"""
          println(params.map(paramString).mkString("\n"))
          params.find(
            tuple => tuple._1 == "hub.challenge"
          ) match {
            case Some((key, value)) => complete(value)
            case None => complete(StatusCodes.OK)
          }
        }
      } ~ post {
        entity(as[ByteString]) { payload =>
//          println(payload.utf8String)
          complete {
            fbWebhooksService.onReceiveMessage(payload.utf8String)
            StatusCodes.OK
          }
        }
      }
    }
}
