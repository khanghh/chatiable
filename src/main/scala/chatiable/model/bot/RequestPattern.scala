package chatiable.model.bot

final case class RequestPattern(
  askPattern: String,
  request: String
)

object RequestPattern {
  val tupled = (this.apply _).tupled
}