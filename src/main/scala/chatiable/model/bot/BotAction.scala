package chatiable.model.bot

final case class BotAction(
  askPattern: String,
  action: String
)

object BotAction {
  final case class CheckWeather(city: String, time: String)
  final case class DoMath(equation: String)
  val tupled = (this.apply _).tupled
}