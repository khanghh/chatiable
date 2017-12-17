package chatiable.server

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorMaterializer
import chatiable.persistence.repository.BotReplyRepository
import chatiable.persistence.repository.MessengerUserRepository
import chatiable.persistence.repository.RequestPatternRepository
import chatiable.server.bot.BotServer
import chatiable.server.facebook.WebhooksServer
import chatiable.server.handler.PrintParameterHandler
import chatiable.service.FBPageService
import chatiable.service.bot.BotReplyService
import chatiable.service.bot.UserRequsetService
import chatiable.service.facebook.message.FBWebhooksService
import chatiable.service.math.CCHttpClient
import chatiable.service.math.CCMathService
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant.Answer
import chatiable.service.user.PVPChatService
import chatiable.service.user.UserService
import chatiable.service.math.CocCocMathApi
import chatiable.service.weather.OpenWeatherService
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration
import scala.io.StdIn

object ChatiableServer extends App {
  override def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("chatiable")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
    implicit val http: HttpExt = Http()

    lazy val database = Database.forConfig("chatiable.mysql")
    lazy val botReplyRepo = new BotReplyRepository(database)
    lazy val requestPatternRepo = new RequestPatternRepository(database)
    lazy val messengerUserRepo = new MessengerUserRepository(database)

    lazy val userService = new UserService(messengerUserRepo)
    lazy val fBPageService = new FBPageService(ChatiableServerConfig.fbAccessToken)
    lazy val botActionService = new UserRequsetService(requestPatternRepo)
    lazy val botReplyService = new BotReplyService(botReplyRepo)
    lazy val pvpChatService = new PVPChatService()
    lazy val fBWebhooksService = new FBWebhooksService()
    lazy val ccMathService = new CCMathService()
    lazy val openWeatherService = new OpenWeatherService()

    lazy val botServer = new BotServer(
      botActionService,
      botReplyService,
      pvpChatService,
      userService,
      fBPageService,
      fBWebhooksService,
      ccMathService,
      openWeatherService
    )

    lazy val webhookServer = new WebhooksServer(fBWebhooksService)

    lazy val printParameterHandler = new PrintParameterHandler
    lazy val exceptionHandler = ExceptionHandler {
      case throwble: Throwable =>
        throwble.printStackTrace()
        complete(StatusCodes.InternalServerError: StatusCode)
    }

    lazy val routeHandlers = Seq(
      botServer,
      webhookServer
    )

    lazy val serverRoute = routeHandlers
      .map(_.route)
      .reduceOption(_ ~ _)
      .map(handleExceptions(exceptionHandler)(_))

    lazy val printParamsRoute = printParameterHandler.route

    val route = serverRoute.fold(printParamsRoute)(printParamsRoute ~ _)

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

    println(s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop...")

//    implicit val ccHttpClient: CCHttpClient = new CCHttpClient()
//    val future = CocCocMathApi.getMathResult("dfbfdb").map { response =>
//      response.math.variants match {
//        case Some(variants) =>
//          println("error")
//        case None =>
//          println("none")
//      }
//    }
//    Await.result(future, Duration.Inf)

//    Await.result(
//      for {
//        _ <- botReplyRepo.createSchema
//        _ <- messengerUserRepo.createSchema
//        _ <- requestPatternRepo.createSchema
//      } yield(),
//      Duration.Inf
//    )

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done

  }
}
