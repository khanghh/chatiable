package chatiable.server.handler

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.model.chatfuel.Messages
import chatiable.model.chatfuel.Messages.Message
import io.circe.Printer

class NewChatHandler extends RouteHandler {
  def route: Route =
    path("newchat") {
      get {
        println("new chat")
        parameters(
          "selected gender".as[String],
          "last name".as[String],
          "first name".as[String],
          "messenger user id".as[String],
          "gender".as[String]
        ) { (selectedgender, lastname, firstName, id, gender) =>
          selectedgender match {
            case _ =>
              val data = Printer.spaces2.copy(dropNullKeys = true).pretty(
                Messages.encoder.apply(
                  Messages(
                    List(
                      Message(None, Some(s"Đã chọn ${selectedgender}")),
                    )
                  )
                )
              )
              complete(data)
          }
        }
      }
    }
}