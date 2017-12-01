package chatiable.server.handler

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.server.bot.BotChatServer.HandleMessage
import chatiable.service.chatfuel.ChatfuelApi
import chatiable.service.facebook.FBPageApi

class ChatBotHandler(
  botChatServer: ActorRef
) extends RouteHandler {
  override def route: Route =
    path("bot") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String],
        ) { (text, userId) =>
          text match {
            case "bye" =>
              complete(ChatfuelApi.sendSilent)
            case _ =>
              botChatServer ! HandleMessage(userId, text)
              complete(ChatfuelApi.sendSilent)
          }
        }
      }
    }
}
