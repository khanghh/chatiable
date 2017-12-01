package chatiable.persistence.table

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import BotActions._
import chatiable.model.bot.BotAction

class BotActions(tag: Tag) extends Table[BotAction](tag, "BotActions") {
  def askPattern = column[String]("AskPattern", O.Length(askPatternMaxLength))
  def action = column[String]("Action", O.Length(actionMaxLength))
  def idx = index("unique_bot_actions", (askPattern, action), unique = true)
  def `*`: ProvenShape[BotAction] = (askPattern, action) <> (BotAction.tupled, BotAction.unapply)
}

object BotActions {
  val askPatternMaxLength = 100
  val actionMaxLength = 100
  val query = TableQuery[BotActions]
}