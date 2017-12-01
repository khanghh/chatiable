package chatiable.server.handler

import akka.actor.ActorRef
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.model.user.MessengerUser
import chatiable.server.user.PVPChatServer.StartPair
import chatiable.service.chatfuel.ChatfuelApi

class NewChatHandler(
  pvpChatServer: ActorRef
) extends RouteHandler {
  def route: Route =
    path("newchat") {
      get {
        println("new chat")
        parameters(
          "selected gender".as[String],
          "last name".as[String],
          "first name".as[String],
          "messenger user id".as[String],
          "gender".as[String],
        ) { (selectedGender, lastName, firstName, userId, gender) =>
          val user = MessengerUser(
            userId = userId,
            gender = gender.equals("male"),
            selectedGender = selectedGender.toLowerCase.equals("boy")
          )

          selectedGender.toLowerCase match {
            case "boy" =>
              pvpChatServer ! StartPair(user)
              complete(ChatfuelApi.sendTextMessage(s"Đang tìm kiếm. Vui lòng chờ chút :)"))
            case "girl" =>
              pvpChatServer ! StartPair(user)
              complete(ChatfuelApi.sendTextMessage(s"Đang tìm kiếm. Vui lòng chờ chút :)"))
            case _ =>
              complete(ChatfuelApi.redirect("Default answer"))
          }
        }
      }
    }
}
