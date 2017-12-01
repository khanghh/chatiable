package chatiable.persistence.repository

import java.sql.SQLIntegrityConstraintViolationException

import chatiable.model.bot.BotAction
import chatiable.persistence.table.BotActions
import com.mysql.cj.jdbc.exceptions.MySQLQueryInterruptedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// scalastyle:off underscore.import
import slick.jdbc.MySQLProfile.api._
// scalastyle:on underscore.import

final class BotActionRepository(
  database: Database
) {

  def add(ask: String, reply: String, propbabl: Int): Future[Unit] = {
    database.run(DBIO.seq(
      BotActions.query += BotAction(ask, reply)
    )).recoverWith{
      case uniqeEx: SQLIntegrityConstraintViolationException =>
        Future.successful(println("record already exists !"))
      case ex: Throwable => Future.failed(ex)
    }
  }

  def get: Future[Seq[BotAction]] = {
    database
      .run {
        BotActions.query.result
      }
  }

}

