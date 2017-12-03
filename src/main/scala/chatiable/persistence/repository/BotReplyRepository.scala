package chatiable.persistence.repository

import java.sql.SQLIntegrityConstraintViolationException

import chatiable.model.bot.BotReply
import chatiable.persistence.table.BotReplies
import com.mysql.cj.jdbc.exceptions.MySQLQueryInterruptedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// scalastyle:off underscore.import
import slick.jdbc.MySQLProfile.api._
// scalastyle:on underscore.import

final class BotReplyRepository(
  database: Database
) {

  def add(ask: String, reply: String, propbabl: Int): Future[Unit] = {
    database.run(DBIO.seq(
      BotReplies.query += BotReply(ask, reply, propbabl)
    )).recoverWith{
      case uniqeEx: SQLIntegrityConstraintViolationException =>
        Future.successful(println("record already exists !"))
      case ex => Future.failed(ex)
    }
  }

  private[this] def getQuery(ask: String) = {
    BotReplies.query.filter(_.ask === ask)
  }

  def get(ask: String): Future[Seq[BotReply]] = {
    database.run(getQuery(ask).result)
  }

  def createSchema: Future[Unit] = {
    database.run(
      BotReplies.query.schema.create
    )
  }

}

