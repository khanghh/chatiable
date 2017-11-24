package chatiable.server.handler

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import chatiable.model.chatfuel.Messages
import chatiable.model.chatfuel.Messages.Message
import io.circe.Printer

class ChatMaleHandler extends RouteHandler {
  override def route: Route =
    path("male") {
      get {
        parameters(
          "text".as[String],
          "last name".as[String],
          "first name".as[String],
          "messenger user id".as[String],
          "gender".as[String]
        ) { (text, lastname, firstName, id, gender) =>
          text match {
            case _ =>
              val data = Printer.spaces2.copy(dropNullKeys = true).pretty(
                Messages.encoder.apply(
                  Messages(
                    List(
                      Message(None, Some("Đang chat với boy")),
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
