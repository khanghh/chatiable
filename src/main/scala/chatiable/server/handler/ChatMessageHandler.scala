package chatiable.server.handler

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.server.user.PVPChatServer
import chatiable.server.user.PVPChatServer.SendChatMessage
import chatiable.server.user.PVPChatServer.UserEndChat
import chatiable.service.chatfuel.ChatfuelApi

class ChatMessageHandler(
  pvpChatServer: ActorRef
) extends RouteHandler {
  override def route: Route =
    path("message") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String],
        ) { (text, userId) =>
          text match {
            case "bye" =>
              pvpChatServer ! SendChatMessage(userId, text)
              pvpChatServer ! UserEndChat(userId)
              complete(ChatfuelApi.sendSilent)
            case _ =>
              PVPChatServer.getUserById(userId) match {
                case Some(messageUser) =>
                  pvpChatServer ! SendChatMessage(userId, text)
                  complete(ChatfuelApi.sendSilent)
                case None =>
                  complete(ChatfuelApi.redirect("Default answer"))
              }
          }
        }
      }
    }
}
