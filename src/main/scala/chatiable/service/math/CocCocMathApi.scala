package chatiable.service.math

import akka.http.scaladsl.model.HttpMethods

import scala.concurrent.Future

object CocCocMathApi {
  def getMathResult(
    message: String
  )(implicit ccHttpClient: CCHttpClient): Future[CCSendSolveMathResponse] = {
    ccHttpClient.request[CCSendSolveMathResponse](
      HttpMethods.GET,
      "composer/math",
      Map("q" -> message)
    )
  }
}
