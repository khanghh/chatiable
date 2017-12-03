package chatiable.server.bot

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import chatiable.model.user.MessengerUser
import chatiable.server.Server
import chatiable.service.FBPageService
import chatiable.service.chatfuel.ChatfuelApi
import chatiable.service.facebook.FBHttpClient
import chatiable.service.user.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class BotReplyServer(
  userService: UserService,
  fbPageService: FBPageService
) extends Server {
  override def route: Route =
    path("message") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String]
        ) { (message, userId) =>
          for {
            messengerUser <- getUser(userId)
          } yield {

          }
          complete(ChatfuelApi.sendSilent)
        }
      }
    }

  def getUser(userId: String): Future[MessengerUser] ={
    userService.getUser(userId).flatMap {
      case Some(user) =>
        Future(user)
      case None =>
        for {
          userInfo <- fbPageService.getUserInfo(userId)
          messengerUser = MessengerUser(userInfo.id, userInfo.name, userInfo.gender.equals("male"))
          _ <- userService.addUser(messengerUser)
        } yield messengerUser
    }
  }

  def handleMessage(user: MessengerUser, message: String): Future[Unit] = {
  }
}
