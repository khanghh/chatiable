package chatiable.server.bot

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.UserRequest
import chatiable.model.user.request.math.CocCocMathRequest
import chatiable.model.user.request.pvpchat.PVPChatRequest
import chatiable.model.user.request.weather.OpenWeatherRequest
import chatiable.server.Server
import chatiable.service.FBPageService
import chatiable.service.bot.BotReplyService
import chatiable.service.bot.UserRequsetService
import chatiable.service.chatfuel.ChatfuelApi
import chatiable.service.facebook.message.FBWebhooksService
import chatiable.service.facebook.message.IncommingWebhooksMessage
import chatiable.service.facebook.message.IncommingWebhooksMessage.Entry.Message
import chatiable.service.math.CCMathService
import chatiable.service.user.PVPChatService
import chatiable.service.user.UserService
import chatiable.service.weather.OpenWeatherService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class BotServer(
  userRequsetService: UserRequsetService,
  botReplyService: BotReplyService,
  pvpChatService: PVPChatService,
  userService: UserService,
  fbPageService: FBPageService,
  fbWebhooksService: FBWebhooksService,
  ccMathService: CCMathService,
  openWeatherService: OpenWeatherService
) extends Server {

  fbWebhooksService.setProcessMessage(handleWebhooksMessage)

  override def route: Route =
    path("message") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String]
        ) { (message, userId) =>
          complete {
            for {
              user <- getUser(userId)
              _ <- handleMessage(user, message)
            } yield {
              ChatfuelApi.sendSilent
            }
          }
        }
      }
    }


  private[this] def handleWebhooksMessage(
    webhooksMessage: IncommingWebhooksMessage
  ): Future[Unit] = {
//    def processMessage(listMsg: Seq[IncommingWebhooksMessage.Entry.Messaging]): Future[Unit] = {
//      listMsg match {
//        case messaging :: tail =>
//          getUser(messaging.sender.id).flatMap { user =>
//            handleMessage(user, messaging.message)
//          }.flatMap(_ => processMessage(tail))
//        case Seq() =>
//          Future.successful()
//      }
//    }
//
//    processMessage(webhooksMessage.entry.head.messaging)
    Future()
  }

  private[this] def getUser(userId: String): Future[MessengerUser] = {
    userService.getUser(userId).flatMap {
      case Some(user) => Future(user)
      case None =>
        fbPageService.getUserInfo(userId).flatMap { userInfo =>
          val user = MessengerUser(
            userInfo.id,
            userInfo.name,
            userInfo.gender.equals("male")
          )
          userService.addUser(user).map(_ => user)
        }
    }
  }

  private[this] def getUserRequest(user: MessengerUser, message: String): Future[UserRequest] = {
    user.request match {
      case null =>
        userRequsetService.getUserRequest(message).map(request => {
          user.request = request
          user.request
        })
      case _ => Future(user.request)
    }
  }

  private[this] def handleMessage(user: MessengerUser, message: String): Future[_] = {
    val deltaTime = System.currentTimeMillis() - user.lastMgsMilis
    user.lastMgsMilis = System.currentTimeMillis()
    if (deltaTime > 1000) {
      for {
        _ <- userRequsetService.handleAddPattern(user, message)
        _ <- getUserRequest(user, message).flatMap {
          case req: PVPChatRequest =>
            pvpChatService.handleMessage(user, message)
          case req: CocCocMathRequest =>
            ccMathService.handleMessage(user, message)
          case req: OpenWeatherRequest =>
            openWeatherService.handleMessage(user, message)
          case _ =>
            botReplyService.handleMessage(user, message)
        }
      } yield ()
    } else {
      Future.successful(println(s"${user.userId} chat qua nhanh !"))
    }
  }
}
