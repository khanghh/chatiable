package chatiable.persistence.repository

import java.sql.SQLIntegrityConstraintViolationException

import chatiable.model.bot.RequestPattern
import chatiable.persistence.table.RequestPatterns

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// scalastyle:off underscore.import
import slick.jdbc.MySQLProfile.api._
// scalastyle:on underscore.import

final class RequestPatternRepository(
  database: Database
) {

  def add(ask: String, reply: String): Future[Unit] = {
    database.run(DBIO.seq(
      RequestPatterns.query += RequestPattern(ask, reply)
    )).recoverWith{
      case uniqeEx: SQLIntegrityConstraintViolationException =>
        Future.successful(println("record already exists !"))
      case ex: Throwable => Future.failed(ex)
    }
  }

  def get: Future[Seq[RequestPattern]] = {
    database
      .run {
        RequestPatterns.query.result
      }
  }

  def createSchema: Future[Unit] = {
    database.run(
      RequestPatterns.query.schema.create
    )
  }

}

