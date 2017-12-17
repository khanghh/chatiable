package chatiable.model.user.request.weather

import chatiable.model.user.request.UserRequest

abstract class OpenWeatherRequest() extends UserRequest

object OpenWeatherRequest {
  final case class NewCheckWeather() extends OpenWeatherRequest
  final case class UserInputCity() extends OpenWeatherRequest
  final case class GetCurrentWeather(city: String) extends OpenWeatherRequest
  final case class GetWeatherByDate(city: String, date: String) extends OpenWeatherRequest
  final case class GetForecast(city: String) extends OpenWeatherRequest
}