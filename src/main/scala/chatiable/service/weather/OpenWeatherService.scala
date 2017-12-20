package chatiable.service.weather


import akka.http.scaladsl.HttpExt
import akka.stream.ActorMaterializer
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.weather.OpenWeatherRequest.GetCurrentWeather
import chatiable.model.user.request.weather.OpenWeatherRequest.GetForecastByDate
import chatiable.model.user.request.weather.OpenWeatherRequest.NewCheckWeather
import chatiable.model.user.request.weather.OpenWeatherRequest.UserInputCity
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.SenderActions
import chatiable.utils.StringUtils._
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

final class OpenWeatherService(
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  def handleMessage(user: MessengerUser, message: String): Future[_] = {
    user.request match {
      case req: NewCheckWeather => handleNewCheckWeather(user, message)
      case req: UserInputCity => handleUserInputCity(user, message)
      case req: GetCurrentWeather => handleGetCurrentWeather(user, message)
      case req: GetForecastByDate => handleGetWeatherByDate(user, message)
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
    user.request = GetCurrentWeather(message)
    handleGetCurrentWeather(user, message)
  }

  def handleGetCurrentWeather(user: MessengerUser, message: String): Future[_] = {
    println("getcurrent")
    implicit val owHttpClient: OpenWeatherHttpClient = new OpenWeatherHttpClient(ChatiableServerConfig.openWeatherAPIkey)
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    val city = user.request.asInstanceOf[GetCurrentWeather].city.withoutToneMarks()
    OpenWeatherApi.getCurrentWeather(city).flatMap { response =>
      val weatherMsg = s"Thông tin thời tiết hiện tại ở ${response.name}:"
        .concat(s"\nMô tả: ${response.weather.head.description}")
        .concat(s"\nNhiệt độ trung bình: ${response.main.temp}℃")
        .concat(s"\nNhiệt độ thấp nhất: ${response.main.temp_min}℃")
        .concat(s"\nNhiệt độ cao nhất: ${response.main.temp_max}℃")
        .concat(s"\nĐộ ẩm: ${response.main.humidity}%")
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
        .concat(s"\nMây bao phủ: ${response.clouds.all}%")
        .concat("\nThông tin được cung cấp bởi openweathermap.com")
      for {
        _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
        _ <- FBPageApi.sendTextMessage(user.userId, weatherMsg)
      } yield user.request = null
    }.recoverWith {
      case ex: Throwable =>
        for {
          _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
          _ <- FBPageApi.sendTextMessage(user.userId, s"Không tìm thấy thông tin thời tiết ở $city ")
        } yield user.request = null
    }
  }

  def handleGetWeatherByDate(user: MessengerUser, message: String): Future[_] = {
    val req = user.request.asInstanceOf[GetForecastByDate]
    implicit val owHttpClient: OpenWeatherHttpClient = new OpenWeatherHttpClient(ChatiableServerConfig.openWeatherAPIkey)
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    println(req.city)
    println(req.date)
    def getDate(dateStr: String): Option[LocalDate] = {
      dateStr.toLowerCase() match {
        case "hôm nay" => Some(new LocalDate(System.currentTimeMillis(), DateTimeZone.getDefault))
        case "ngày mai" => Some(new LocalDate(System.currentTimeMillis() + 86400000, DateTimeZone.getDefault))
        case _ =>
          Try[LocalDate](DateTimeFormat.forPattern("dd/MM/yyyy").withZone(DateTimeZone.getDefault).parseLocalDate(req.date))
            .orElse(Try[LocalDate](DateTimeFormat.forPattern("dd-MM-yyyy").withZone(DateTimeZone.getDefault).parseLocalDate(req.date))) match {
            case Success(date) => Some(date)
            case Failure(ex) => None
          }
      }
    }

    getDate(req.date) match {
      case Some(date) =>
        OpenWeatherApi.getForecasts(req.city.withoutToneMarks()).flatMap { response =>
          response.list.find { weatherDayInfo =>
            date.compareTo(new LocalDate(weatherDayInfo.dt * 1000, DateTimeZone.getDefault)) == 0
          } match {
            case Some(info) =>
              val dateStr = DateTimeFormat.forPattern("dd/MM/yyyy").withZone(DateTimeZone.getDefault).print(info.dt * 1000)
              val weatherMsg = s"Thông tin dự báo thời tiết ${response.city.name} ngày $dateStr"
                .concat(s"\nMô tả: ${info.weather.head.description}")
                .concat(s"\nNhiệt độ trung bình: ${info.temp.day}℃")
                .concat(s"\nNhiệt độ thấp nhất: ${info.temp.min}℃")
                .concat(s"\nNhiệt độ cao nhất: ${info.temp.max}℃")
                .concat(s"\nĐộ ẩm: ${info.humidity}%")
                .concat(s"\nVận tốc gió: ${info.speed} m/s")
                .concat(s"\nHướng gió: ${info.deg} độ")
                .concat(s"\nMây bao phủ: ${info.clouds}%")
                .concat("\nThông tin được cung cấp bởi openweathermap.com")
              for {
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
                _ <- FBPageApi.sendTextMessage(user.userId, weatherMsg)
              } yield user.request = null
            case None =>
              for {
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
                _ <- FBPageApi.sendTextMessage(user.userId, s"Không tìm thấy dự báo thời tiết ở ${req.city} ngày ${req.date}")
              } yield ()
          }
        }.recoverWith {
          case ex: Throwable =>
            for {
              _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
              _ <- FBPageApi.sendTextMessage(user.userId, s"Không tìm thấy thông tin thời tiết ở ${req.city} ")
            } yield user.request = null
        }
      case None =>
        for {
          _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
          _ <- FBPageApi.sendTextMessage(user.userId, "ngày không họp lệ")
        } yield ()

    }

  }
}
