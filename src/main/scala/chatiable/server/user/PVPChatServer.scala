package chatiable.server.user

import akka.actor.Actor
import chatiable.model.user.MessengerUser
import chatiable.server.user.PVPChatServer._
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi

import scala.collection.mutable
class PVPChatServer()(implicit fbHttpClient: FBHttpClient) extends Actor {

  private val watingUserList: mutable.ListBuffer[MessengerUser] = mutable.ListBuffer[MessengerUser]()

  def receive: Receive = {
    case StartPair(user: MessengerUser) =>
      watingUserList.find(watingUser =>
        watingUser.gender.equals(user.selectedGender)
        && watingUser.selectedGender.equals(user.gender)
      ) match {
        case Some(friend) =>
          userChatPairs += ((user.userId, friend.userId))
          FBPageApi.sendTextMessage(user.userId, "hi")
          FBPageApi.sendTextMessage(friend.userId, "hi")
          println(s"Pair: ${user.userId} <-> ${friend.userId}")
          watingUserList -= friend
        case None =>
          watingUserList += user
      }
    case SendChatMessage(senderId, message) =>
      watingUserList.find(_.userId.equals(senderId)) match {
        case Some(user) =>
          FBPageApi.sendTextMessage(senderId, "Đang tìm kiếm vui lòng chờ chút :)")
        case None =>
          userChatPairs.find(pair => pair._1.equals(senderId) || pair._2.equals(senderId)) match {
            case Some((userId1, userId2)) =>
              val receiverId = if (senderId.equals(userId1)) userId2 else userId1
              message match {
                case url if (message.matches("\\S+") && (message.startsWith("http://") || message.startsWith("https://"))) =>
                  url.split('?').apply(0).split('.').lastOption match {
                    case Some(ext) =>
                      ext match {
                        case "png" | "jpg" | "gif" =>
                          FBPageApi.sendAttachment(receiverId, "image", url)
                        case "avi" | "mp4" =>
                          FBPageApi.sendAttachment(receiverId, "video", url)
                        case "mp3" | "wav" =>
                          FBPageApi.sendAttachment(receiverId, "audio", url)
                        case _ =>
                          FBPageApi.sendAttachment(receiverId, "file", url)
                      }
                    case None => FBPageApi.sendTextMessage(receiverId, message)
                  }
                case _ =>
                  FBPageApi.sendTextMessage(receiverId, message)
              }
            case None =>
//              if (!message.equals("bye")) {
//                FBPageApi.sendTextMessage(senderId, "Cuộc trò chuyện đã kết thúc. Chat \"bye\" để thoát.")
//              }
          }
      }
    case EndChat(userId) =>
      userChatPairs.find(pair => pair._1.equals(userId) || pair._2.equals(userId)) match {
        case Some(pair) => userChatPairs -= pair
        case None =>
      }
  }
}

object PVPChatServer {
  final case class StartPair(user: MessengerUser)
  final case class SendChatMessage(userId: String, message: String)
  final case class EndChat(userId: String)
  private val userChatPairs: mutable.ListBuffer[(String, String)] = mutable.ListBuffer[(String, String)]()
  def getPairByUserId(userId: String): Option[(String, String)] = userChatPairs.find(pair => pair._1.equals(userId) || pair._2.equals(userId))
}
