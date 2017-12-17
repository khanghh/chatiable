package chatiable.service.weather

import akka.http.scaladsl.model.HttpMethods

import scala.concurrent.Future

object OpenWeatherApi {
  def getCurrentWeather(
    city: String
  )(implicit owHttpClient: OpenWeatherHttpClient): Future[OpenWeatherGetCurrentWeatherResponse] = {
    owHttpClient.request[OpenWeatherGetCurrentWeatherResponse](
      HttpMethods.GET,
      "weather",
      Map(
        "q" -> city,
        "lang" -> "vi",
        "units" -> "metric"
      )
    )
  }

  def getForecasts(
    city: String
  )(implicit owHttpClient: OpenWeatherHttpClient): Future[OpenWeatherGetForecastResponse] = {
    owHttpClient.request[OpenWeatherGetForecastResponse](
      HttpMethods.GET,
      "forecast/daily",
      Map(
        "q" -> city,
        "lang" -> "vi",
        "units" -> "metric"
      )
    )
  }
}
