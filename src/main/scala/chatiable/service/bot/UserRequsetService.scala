package chatiable.service.bot

import akka.http.scaladsl.HttpExt
import akka.stream.ActorMaterializer
import chatiable.model.bot.RequestPattern
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.UserRequest
import chatiable.model.user.request.bot.BotReplyRequest
import chatiable.model.user.request.pvpchat.PVPChatRequest
import chatiable.model.user.request.weather.CheckWeatherRequest
import chatiable.persistence.repository.RequestPatternRepository
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class UserRequsetService(
  requestPatternRepo: RequestPatternRepository,
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  def handleAddPattern(user: MessengerUser, message: String): Future[Unit] = {
    implicit val fBHttpClient = new FBHttpClient(ChatiableServerConfig.accessToken)
    val teachPattern = """#addreq (.*)\|(.*)$""".r
    message match {
      case teachPattern(pattern, request) =>
        for {
          _ <- requestPatternRepo.add(pattern, request)
          _ <- FBPageApi.sendTextMessage(user.userId, s"$pattern => $request")
        } yield ()
      case _ => Future.successful()
    }
  }

  def getUserRequest(message: String): Future[UserRequest] = {
    requestPatternRepo.get.map(records => {
      records.collectFirst {
        case pattern: RequestPattern
          if pattern.askPattern.r
            .findAllIn(message).toList
            .nonEmpty => {
            pattern.request match {
              case "pvp_newchat" => PVPChatRequest.NewChat()
              case "pvp_selectedgirl" => PVPChatRequest.SelectedBoy()
              case "pvp_selectedboy" => PVPChatRequest.SelectedGirl()
              case "weather" => CheckWeatherRequest()
            }
          }
      } match {
        case Some(request) =>
          request.asInstanceOf[UserRequest]
        case None =>
          BotReplyRequest()
      }
    })
  }


}