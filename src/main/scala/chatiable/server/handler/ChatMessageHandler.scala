package chatiable.server.handler

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class ChatMessageHandler(
) extends RouteHandler {
  override def route: Route =
    path("message") {
      get {
        parameters(
          "text".as[String],
          "messenger user id".as[String],
        ) { (text, userId) =>
          complete(StatusCodes.OK)
        }
      }
    }
}
