package chatiable.server.handler

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.service.chatfuel.ChatfuelApi
import chatiable.service.chatfuel.Messages
import chatiable.service.chatfuel.Messages.Message
import io.circe.Printer

class ChatBotHandler extends RouteHandler {
  override def route: Route =
    path("bot") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String],
        ) { (text, gender) =>
          text match {
            case "bye" =>
              complete(ChatfuelApi.sendSilent)
            case _ =>
              complete(ChatfuelApi.sendTextMessage("Đang chat với bot"))
          }
        }
      }
    }
}
