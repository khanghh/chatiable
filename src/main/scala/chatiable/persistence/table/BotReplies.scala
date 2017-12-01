package chatiable.persistence.table

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import BotReplies._
import chatiable.model.bot.BotReply

class BotReplies(tag: Tag) extends Table[BotReply](tag, "BotReplies") {
  def ask = column[String]("Ask", O.Length(askMaxLength))
  def reply = column[String]("Reply",O.Length(replyMaxLength))
  def probabl = column[Int]("Probability")
  def idx = index("unique_bot_replies", (ask, reply), unique = true)
  def `*`: ProvenShape[BotReply] = (ask, reply, probabl) <> (BotReply.tupled, BotReply.unapply)
}

object BotReplies {
  val askMaxLength = 100
  val replyMaxLength = 200
  val query = TableQuery[BotReplies]
}