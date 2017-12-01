package chatiable.server

import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorMaterializer
import chatiable.server.bot.BotChatServer
import chatiable.server.handler.ChatBotHandler
import chatiable.server.handler.ChatMessageHandler
import chatiable.server.handler.NewChatHandler
import chatiable.server.handler.PrintParameterHandler
import chatiable.server.user.PVPChatServer
import chatiable.service.facebook.FBHttpClient
import slick.jdbc.MySQLProfile.api._

import scala.io.StdIn

object ChatiableServer extends App {
  override def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("chatiable")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher
    implicit val http: HttpExt = Http()

    lazy val database = Database.forConfig("chatiable.mysql")

    val accessToken = "EAACW5Fg5N2IBAGQc9VgSzY6Vwmv41hsVIz8MA7fNzAoUldEcTPvnqIzinGGO1KPVZAk917rg6OZBnTfgRog8m0pwvdhH5pf1qXFLC6HzW7GULHUfJyj04terP5AZAkpOR7uDuPAt5yZBnOsYBngsvszQ6PYJStNgTH6nl2IvTAZDZD"
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(accessToken)
    val pvpChatServer = actorSystem.actorOf(Props(new PVPChatServer), "pvpChatServer")
    val botChatServer = actorSystem.actorOf(Props(new BotChatServer(database)), "botChatServer")

    lazy val newChatHandler = new NewChatHandler(pvpChatServer)
    lazy val chatMessageHandler = new ChatMessageHandler(pvpChatServer)
    lazy val chatBotHandler = new ChatBotHandler(botChatServer)
    lazy val printParameterHandler = new PrintParameterHandler
    lazy val exceptionHandler = ExceptionHandler {
      case throwble: Throwable =>
        throwble.printStackTrace()
        complete(StatusCodes.InternalServerError: StatusCode)
    }

    lazy val routeHandlers = Seq(
      chatMessageHandler,
      chatBotHandler,
      newChatHandler
    )

    lazy val serverRoute = routeHandlers
      .map(_.route)
      .reduceOption(_ ~ _)
      .map(handleExceptions(exceptionHandler)(_))

    lazy val printParamsRoute = printParameterHandler.route

    val route = serverRoute.fold(printParamsRoute)(printParamsRoute ~ _)

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

    println(s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop...")

//    botChatServer ! CreateTableIfNotExist

//    botChatServer ! TeachBot("123456", "abc", "ấy", 100)
//    botChatServer ! TeachBot("ngu", "ok", 100)

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done

  }
}
