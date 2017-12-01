package chatiable.server.handler

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.server.user.PVPChatServer
import chatiable.server.user.PVPChatServer.SendChatMessage
import chatiable.server.user.PVPChatServer.EndChat
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
              pvpChatServer ! EndChat(userId)
              complete(ChatfuelApi.sendSilent)
            case _ =>
              pvpChatServer ! SendChatMessage(userId, text)
              PVPChatServer.getPairByUserId(userId) match {
                case Some(pair) =>
                  complete(ChatfuelApi.sendSilent)
                case None =>
                  complete(ChatfuelApi.redirect("Default answer"))
              }
          }
        }
      }
    }
}
