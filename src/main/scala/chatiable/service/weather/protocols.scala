package chatiable.service.weather

import chatiable.service.weather.OpenWeatherGetCurrentWeatherResponse.CloudInfo
import chatiable.service.weather.OpenWeatherGetCurrentWeatherResponse.MainInfo
import chatiable.service.weather.OpenWeatherGetCurrentWeatherResponse.SunInfo
import chatiable.service.weather.OpenWeatherGetCurrentWeatherResponse.WindInfo
import chatiable.service.weather.OpenWeatherGetForecastResponse.WeatherDayInfo.Temperatures
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveDecoder
import circe._

abstract class OWRequest
abstract class OWResponse

final case class OpenWeatherGetForecastResponse(
  city: OpenWeatherGetForecastResponse.CityInfo,
  list: List[OpenWeatherGetForecastResponse.WeatherDayInfo]
) extends OWResponse

object OpenWeatherGetForecastResponse {

  final case class CityInfo(
    id: Int,
    name: String,
    country: String
  )

  object CityInfo {

    implicit val decoder: Decoder[CityInfo] = deriveDecoder
  }

  final case class WeatherDayInfo(
    dt: Long,
    temp: Temperatures,
    pressure: Float,
    humidity: Float,
    weather: List[WeatherDayInfo.WeatherDescription],
    speed: Float,
    deg: Float,
    clouds: Float
  )

  object WeatherDayInfo {

    final case class Temperatures(
      day: Float,
      min: Float,
      max: Float,
      night: Float,
      eve: Float,
      morn: Float
    )

    object Temperatures {

      implicit val decoder: Decoder[Temperatures] = deriveDecoder
    }

    final case class WeatherDescription(
      id: Int,
      main: String,
      description: String,
      icon: String
    )

    object WeatherDescription {

      implicit val decoder: Decoder[WeatherDescription] = deriveDecoder
    }

    implicit val decoder: Decoder[WeatherDayInfo] = deriveDecoder
  }

  implicit val decoder: Decoder[OpenWeatherGetForecastResponse] = deriveDecoder
}

final case class OpenWeatherGetCurrentWeatherResponse(
  weather: List[OpenWeatherGetCurrentWeatherResponse.WeatherDescription],
  main: MainInfo,
  visibility: Option[Int],
  wind: WindInfo,
  clouds: CloudInfo,
  dt: Long,
  sys: SunInfo,
  id: Int,
  name: String
) extends OWResponse

object OpenWeatherGetCurrentWeatherResponse {

  final case class WeatherDescription(
    id: Int,
    main: String,
    description: String,
    icon: String
  )

  object WeatherDescription {

    implicit val decoder: Decoder[WeatherDescription] = deriveDecoder
  }

  final case class MainInfo(
    temp: Float,
    pressure: Float,
    humidity: Float,
    temp_min: Float,
    temp_max: Float
  )

  object MainInfo {

    implicit val decoder: Decoder[MainInfo] = deriveDecoder
  }

  final case class WindInfo(
    speed: Float,
    deg: Option[Float]
  )

  object WindInfo {

    implicit val decoder: Decoder[WindInfo] = deriveDecoder
  }

  final case class CloudInfo(
    all: Float
  )

  object CloudInfo {

    implicit val decoder: Decoder[CloudInfo] = deriveDecoder
  }

  final case class SunInfo(
    country: String,
    sunrise: Long,
    sunset: Long
  )

  object SunInfo {

    implicit val decoder: Decoder[SunInfo] = deriveDecoder
  }

  implicit val decoder: Decoder[OpenWeatherGetCurrentWeatherResponse] = deriveDecoder
}