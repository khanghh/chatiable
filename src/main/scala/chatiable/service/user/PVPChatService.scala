package chatiable.service.user

import akka.http.scaladsl.HttpExt
import akka.stream.ActorMaterializer
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.pvpchat.PVPChatRequest._
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.SenderActions
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class PVPChatService(
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  def handleMessage(user: MessengerUser, message: String): Future[_] = {
    user.request match {
      case NewChat() => handleNewChat(user, message)
      case SelectGender() => handleSelectGender(user, message)
      case SelectedBoy() => handleSelectedBoy(user, message)
      case SelectedGirl() => handleSelectedGirl(user, message)
      case FindingFriend(_) => handleFindingFriend(user, message)
      case Pairing(_) => handlePairing(user, message)
    }
  }

  private[this] def handleNewChat(user: MessengerUser, message: String): Future[_] = {
    implicit val fBHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    user.request = SelectGender()
    for {
      _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn).map(_ => Thread.sleep(1000))
      _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOff)
      _ <- FBPageApi.sendQuickReplies(user.userId, "Bạn muốn chat với", List("Boy", "Girl"))
    } yield ()
  }

  private[this] def handleSelectGender(user: MessengerUser, message: String): Future[_] = {
    implicit val fBHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    message match {
      case "Boy" =>
        handleSelectedBoy(user, message)
      case "Girl" =>
        handleSelectedGirl(user, message)
      case _ =>
        user.request = null
        FBPageApi.sendTextMessage(user.userId, ":)")
    }
  }

  private[this] def handleSelectedGirl(user: MessengerUser, message: String): Future[_] = {
    startPair(user, false)
  }

  private[this] def handleSelectedBoy(user: MessengerUser, message: String): Future[_] = {
    startPair(user, true)
  }

  private[this] def handleFindingFriend(user: MessengerUser, message: String): Future[_] = {
    implicit val fBHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    message match {
      case "bye" =>
        stopPair(user)
        FBPageApi.sendTextMessage(user.userId, "OK! Bye :)")
      case _ =>
        for {
          _ <- FBPageApi.sendTextMessage(user.userId, "Đang tìm kiếm vui lòng chờ chút...")
          _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
        } yield ()
    }
  }

  private[this] def handlePairing(user: MessengerUser, message: String): Future[_] = {
    implicit val fBHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    val friend = user.request.asInstanceOf[Pairing].friend
    message match {
      case url if message.matches("\\S+") && (message.startsWith("http://") || message.startsWith("https://")) =>
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
      case "bye" =>
        stopPair(user)
        for {
          _ <- FBPageApi.sendTextMessage(friend.userId, message)
          _ <- FBPageApi.sendTextMessage(user.userId, "OK! Bye :)")
        } yield()
      case _ =>
        FBPageApi.sendTextMessage(friend.userId, message)
    }
  }

  private[this] def startPair(user: MessengerUser, userSelectedGender: Boolean): Future[_] = {
    implicit val fBHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    UserManager.onlineUsers.synchronized {
      UserManager.onlineUsers.find { friend =>
        friend.request match {
          case friendReq: FindingFriend =>
            friend.gender == userSelectedGender &&
              user.gender == friendReq.selectedGender
          case _ => false
        }
      } match {
        case Some(friend) =>
          user.request = Pairing(friend)
          friend.request = Pairing(user)
          for {
            _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn).map(_ => Thread.sleep(1000))
            _ <- FBPageApi.sendTextMessage(user.userId, "Ghép cặp thành công. Bạn đã bắt đầu cuộc trò chuyện bí mật.")
            _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOff)
            _ <- FBPageApi.sendSenderAction(friend.userId, SenderActions.TypingOn).map(_ => Thread.sleep(1000))
            _ <- FBPageApi.sendTextMessage(friend.userId, "Ghép cặp thành công. Bạn đã bắt đầu cuộc trò chuyện bí mật.")
            _ <- FBPageApi.sendSenderAction(friend.userId, SenderActions.TypingOff)
          } yield ()
        case None =>
          user.request = FindingFriend(userSelectedGender)
          for {
            _ <- FBPageApi.sendTextMessage(user.userId, "Đang tìm kiếm vui lòng chờ chút...")
            _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
          } yield ()
      }
    }
  }

  private[this] def stopPair(user: MessengerUser): Unit = {
    UserManager.onlineUsers.synchronized {
      user.request match {
        case FindingFriend(selectedGender) =>
          user.request = null
        case Pairing(friend) =>
          user.request = null
          friend.request = null
      }
    }
  }
}
