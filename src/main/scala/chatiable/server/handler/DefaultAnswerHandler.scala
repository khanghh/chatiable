package chatiable.server.handler
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class DefaultAnswerHandler extends RouteHandler {
  override def route: Route =
    path("default") {
      get {
        parameters(
          "user freeform input".as[String],
          "chatfuel user id".as[String],
          "first name".as[String],
          "last name".as[String],
          "gender".as[String]
        ) { (input, id, firstName, lastName, gender) =>
          input match {
            case "hi" =>
              complete("dbndfbf")
          }
        }
      }
    }
}
