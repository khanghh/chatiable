package chatiable.persistence.table

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import RequestPatterns._
import chatiable.model.bot.RequestPattern

class RequestPatterns(tag: Tag) extends Table[RequestPattern](tag, "RequestPatterns") {
  def askPattern = column[String]("AskPattern", O.Length(askPatternMaxLength))
  def action = column[String]("Request", O.Length(actionMaxLength))
  def idx = index("unique_request_patterns", (askPattern, action), unique = true)
  def `*`: ProvenShape[RequestPattern] = (askPattern, action) <> (RequestPattern.tupled, RequestPattern.unapply)
}

object RequestPatterns {
  val askPatternMaxLength = 100
  val actionMaxLength = 100
  val query = TableQuery[RequestPatterns]
}