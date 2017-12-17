package chatiable.service.weather

import scala.collection.immutable
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.coding.Deflate
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.coding.NoCoding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import io.circe.syntax._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import io.circe.Encoder
import io.circe.Decoder

import scala.concurrent.Future
import chatiable.service.weather.OpenWeatherHttpClient._

import scala.concurrent.ExecutionContext.Implicits.global
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

final class OpenWeatherHttpClient(
  appAPIkey: String
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  private[this] def constructRequest(
    method: HttpMethod,
    path: String,
    entity: HttpEntity.Strict = HttpEntity.Empty,
    queries: Map[String, String] = Map.empty
  ): HttpRequest = {
    HttpRequest(
      method = method,
      uri = Uri(path).resolvedAgainst(BaseUri).withQuery(Query(queries)),
      headers = BaseHeaders,
      entity = entity
    )
  }

  private[this] def decodeResponse[B <: OWResponse](
    response: HttpResponse
  )(implicit
    decoder: Decoder[B]
  ): Future[B] = {
    val entity = response.entity.withContentType(MediaTypes.`application/json`)
    val unmarshal = Unmarshal(entity)
    unmarshal.to[B]
  }

  private[this] def request(request: HttpRequest): Future[HttpResponse] = {
    val uriWithAccessToken = request.uri.withQuery(
      request.uri.query().+:("appid" -> appAPIkey)
    )
    for {
      response <- http
        .singleRequest(request.copy(uri = uriWithAccessToken))
          .recoverWith {
          case throwable: Throwable =>
              Future.failed(throwable)
        }
    } yield uncompressResponse(response)
  }

  def requestWithoutToken[B <: OWResponse](
    method: HttpMethod,
    path: String,
    queries: Map[String, String]
  )(implicit
    decoder: Decoder[B]
  ): Future[B] = {
    val request = constructRequest(method, path, HttpEntity.Empty, queries)
    for {
      response <- http.singleRequest(request)
      rs = uncompressResponse(response)
      decoded <- decodeResponse[B](rs)
    } yield decoded
  }

  def request[B <: OWResponse](
    method: HttpMethod,
    path: String
  )(implicit
    decoder: Decoder[B]
  ): Future[B] = {
    val request = constructRequest(method, path)
    this
      .request(request)
      .flatMap(response => decodeResponse(response))
  }

  def request[B <: OWResponse](
    method: HttpMethod,
    path: String,
    queries: Map[String, String]
  )(implicit
    decoder: Decoder[B]
  ): Future[B] = {
    val request = constructRequest(method, path, HttpEntity.Empty, queries ++ Map("appid" -> appAPIkey))

    println(request.uri)
    this
      .request(request)
      .flatMap { response =>
        decodeResponse(response)
      }
  }

  def request[A <: OWRequest, B <: OWResponse]( // scalastyle:ignore parameter.number
    method: HttpMethod,
    path: String,
    requestBody: A
  )(implicit
    encoder: Encoder[A],
    decoder: Decoder[B]
  ): Future[B] = {
    val request = constructRequest(
      method,
      path,
      HttpEntity(
        ContentType(MediaTypes.`application/json`),
        requestBody.asJson.noSpaces
      )
    )

    this
      .request(request)
      .flatMap(response => decodeResponse(response))
  }

  def uncompressResponse(response: HttpResponse): HttpResponse = {
    val decoder = response.encoding match {
      case HttpEncodings.gzip => Gzip
      case HttpEncodings.deflate => Deflate
      case _ => NoCoding
    }

    decoder.decodeMessage(response)
  }
}

private object OpenWeatherHttpClient {

  val BaseUri = Uri("http://api.openweathermap.org/data/2.5/")

  val BaseHeaders: immutable.Seq[HttpHeader] = immutable.Seq[HttpHeader](
    Accept(MediaRanges.`*/*`),
    `Accept-Language`(Language("en", "US")),
    `Accept-Encoding`(HttpEncodings.gzip, HttpEncodings.deflate, HttpEncoding.custom("sdch")),
    Connection("close")
  )
}