package chatiable.server.user

import java.net.URL

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.Uri
import chatiable.model.MessengerUser
import chatiable.server.user.PVPChatServer._
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi

import scala.collection.mutable
class PVPChatServer()(implicit fbHttpClient: FBHttpClient) extends Actor {

  def receive: Receive = {
    case StartPair(user: MessengerUser) =>
      onlineUserList.values.find(chatUser =>
        chatUser.chatFriend == null
        && chatUser.gender == user.selectedGender
        && chatUser.selectedGender == user.gender
      ) match {
        case Some(friend) =>
          user.chatFriend = friend
          friend.chatFriend = user
          FBPageApi.sendTextMessage(user.userId, "hi")
          FBPageApi.sendTextMessage(friend.userId, "hi")
          println(s"Pair: ${user.firstName} <-> ${friend.firstName}")
        case None =>
          println("no user")
      }
      onlineUserList += (user.userId -> user)
    case SendChatMessage(userId, message) =>
      if (onlineUserList.contains(userId)) {
        val user: MessengerUser = onlineUserList(userId)
        if (user.chatFriend != null) {
          val friend: MessengerUser = user.chatFriend
          message match {
            case url if (message.matches("\\S+") && (message.startsWith("http://") || message.startsWith("https://")))  =>
              url.split('?').apply(0).split('.').lastOption match {
                case Some(ext) =>
                  ext match {
                    case "png" | "jpg" | "gif" =>
                      FBPageApi.sendAttachment(friend.userId, "image", url)
                    case "avi" | "mp4" =>
                      FBPageApi.sendAttachment(friend.userId, "video", url)
                    case "mp3" | "wav" =>
                      FBPageApi.sendAttachment(friend.userId, "audio", url)
                    case _ =>
                      FBPageApi.sendAttachment(friend.userId, "file", url)
                  }
                case None => FBPageApi.sendTextMessage(friend.userId, message)
              }
            case _ =>
              FBPageApi.sendTextMessage(friend.userId, message)
          }
        } else {
          FBPageApi.sendTextMessage(userId, "Đang tìm kiếm vui lòng chờ chút :)")
        }
      }
    case UserEndChat(userId) =>
      if (onlineUserList.contains(userId)) {
        val user: MessengerUser = onlineUserList(userId)
        onlineUserList.remove(user.userId)
        if (user.chatFriend != null) {
          val friend: MessengerUser = user.chatFriend
          onlineUserList.remove(friend.userId)
        }
      }
  }

  def getUserByUserId(userId: String): MessengerUser = onlineUserList.apply(userId)
}

object PVPChatServer {
  private val onlineUserList: mutable.Map[String, MessengerUser] = mutable.Map[String, MessengerUser]()
  final case class StartPair(user: MessengerUser)
  final case class SendChatMessage(userId: String, message: String)
  final case class UserEndChat(userId: String)
  def getUserById(userId: String): Option[MessengerUser] = onlineUserList.get(userId)
}
