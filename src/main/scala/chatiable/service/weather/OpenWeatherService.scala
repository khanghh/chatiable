package chatiable.service.weather

import akka.http.scaladsl.HttpExt
import akka.stream.ActorMaterializer
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.weather.OpenWeatherRequest.GetCurrentWeather
import chatiable.model.user.request.weather.OpenWeatherRequest.GetForecast
import chatiable.model.user.request.weather.OpenWeatherRequest.GetWeatherByDate
import chatiable.model.user.request.weather.OpenWeatherRequest.NewCheckWeather
import chatiable.model.user.request.weather.OpenWeatherRequest.UserInputCity
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.SenderActions
import scala.concurrent.ExecutionContext.Implicits.global
import chatiable.utils.StringUtils._

import scala.concurrent.Future

final class OpenWeatherService(
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  def handleMessage(user: MessengerUser, message: String): Future[_] = {
    user.request match {
      case NewCheckWeather() => handleNewCheckWeather(user, message)
      case UserInputCity() => handleUserInputCity(user, message)
      case GetCurrentWeather(city) => handleGetCurrentWeather(user, city)
      case GetWeatherByDate(city, date) => handleGetCurrentWeather(user, city)
      case GetForecast(city) => handleGetCurrentWeather(user, city)
    }
  }

  def handleNewCheckWeather(user: MessengerUser, message: String): Future[_] = {
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    for {
      _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
      _ <- FBPageApi.sendTextMessage(user.userId, "Bạn muốn xem thông tin thời tiết ở đâu ?")
    } yield user.request = UserInputCity()
  }

  def handleUserInputCity(user: MessengerUser, message: String): Future[_] = {
    handleGetCurrentWeather(user, message)
  }

  def handleGetCurrentWeather(user: MessengerUser, message: String): Future[_] = {
    println(s"getweather $message")
    implicit val owHttpClient: OpenWeatherHttpClient = new OpenWeatherHttpClient(ChatiableServerConfig.openWeatherAPIkey)
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    val city = message.withoutToneMarks()
    OpenWeatherApi.getCurrentWeather(city).flatMap { response =>
      val weatherMsg = s"Thông tin thời tiết hiện tại ở ${response.name}:"
        .concat(s"\nMô tả: ${response.weather.head.description}")
        .concat(s"\nNhiệt độ trung bình: ${response.main.temp} ℃")
        .concat(s"\nNhiệt độ thấp nhất: ${response.main.temp_min} ℃")
        .concat(s"\nNhiệt độ cao nhất: ${response.main.temp_max} ℃")
        .concat(s"\nĐộ ẩm: ${response.main.humidity} %")
        .concat {
          response.visibility match {
            case Some(value) => s"\nTầm nhìn: $value mét"
            case None => ""
          }
        }
        .concat(s"\nVận tốc gió: ${response.wind.speed} m/s")
        .concat {
          response.wind.deg match {
            case Some(value) => s"\nHướng gió: $value độ"
            case None => ""
          }
        }
        .concat(s"\nMây bao phủ: ${response.clouds.all} %")
      for {
        _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
        _ <- FBPageApi.sendTextMessage(user.userId, weatherMsg)

      } yield user.request = null
    }.recoverWith {
      case ex: Throwable =>
        Future.failed {
          user.request = null
          ex
        }
    }
  }
}
