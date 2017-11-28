package chatiable.service.facebook

import java.net.URLEncoder

import scala.collection.immutable
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import io.circe.syntax._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import io.circe.Encoder

import scala.concurrent.Future
import FBHttpClient._

import scala.concurrent.ExecutionContext.Implicits.global
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Printer

class FBHttpClient(
  accessToken: String
)(implicit
  materializer: ActorMaterializer,
  httpClient: HttpExt
) {

  def request(
    method: HttpMethod,
    path: String,
    requestBody: FBSendMessageRequest
  )(implicit encoder: Encoder[FBSendMessageRequest]): Future[String] = {
    val request = constructRequest(
      method = method,
      path = path,
      HttpEntity(
        ContentType(MediaTypes.`application/json`),
        requestBody.asJson.pretty(Printer.noSpaces.copy(dropNullKeys = true))
      ),
      Map("access_token" -> accessToken)
    )
    for {
      response <- httpClient.singleRequest(request)
      decoded <- decodeRespone(response)
    } yield decoded.messageId
  }

  private[this] def constructRequest(
    method: HttpMethod,
    path: String,
    entity: HttpEntity.Strict = HttpEntity.Empty,
    queries: Map[String, String] = Map.empty
  ): HttpRequest = {
    HttpRequest(
      method = method,
      uri = Uri(
        URLEncoder.encode(path, "UTF-8"),
        Uri.ParsingMode.Relaxed).resolvedAgainst(BaseUri).withQuery(Query(queries)
      ),
      headers = BaseHeaders,
      entity = entity
    )
  }

  def decodeRespone(response: HttpResponse): Future[FBSendMessageRespone] = {
    val entity = response.entity.withContentType(MediaTypes.`application/json`)
    val unmarshal = Unmarshal(entity)
    unmarshal.to[FBSendMessageRespone].recoverWith {
      case exception: Throwable => Future.failed(FBSendMessageException())
    }
  }
}

object FBHttpClient {

  val BaseUri = Uri("https://graph.facebook.com/v2.10/")

  val BaseHeaders = immutable.Seq[HttpHeader](
    Accept(MediaRanges.`*/*`),
    `Accept-Language`(Language("en", "US")),
    `Accept-Encoding`(HttpEncodings.gzip, HttpEncodings.deflate, HttpEncoding.custom("sdch")),
    Connection("close")
  )
}
