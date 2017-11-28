package chatiable.server.bot

import akka.actor.Actor
import chatiable.server.bot.BotChatServer.SendChatMessage
import chatiable.service.facebook.FBHttpClient

class BotChatServer()(implicit fbHttpClient: FBHttpClient) extends Actor {
  override def receive: Receive = {
    case SendChatMessage(userId, message) =>
  }
}

object BotChatServer {
  final case class SendChatMessage(userId: String, message: String)
  final case class UserEndChat(userId: String)
}