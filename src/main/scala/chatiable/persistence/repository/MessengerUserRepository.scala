package chatiable.persistence.repository

import java.sql.SQLIntegrityConstraintViolationException

import chatiable.model.user.MessengerUser
import chatiable.persistence.table.MessengerUsers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// scalastyle:off underscore.import
import slick.jdbc.MySQLProfile.api._
// scalastyle:on underscore.import

final class MessengerUserRepository(
  database: Database
) {

  def add(messengerUser: MessengerUser): Future[Unit] = {
    database.run(DBIO.seq(
       MessengerUsers.query += messengerUser
    )).recoverWith{
      case uniqeEx: SQLIntegrityConstraintViolationException =>
        Future.successful(println("record already exists !"))
      case ex => Future.failed(ex)
    }
  }

  private[this] def getQuery(userId: String) = {
     MessengerUsers.query.filter(_.id === userId)
  }

  def get(userId: String): Future[Option[MessengerUser]] = {
    database.run(getQuery(userId).result.headOption)
  }

  def createSchema: Future[Unit] = {
    database.run(
      MessengerUsers.query.schema.create
    )
  }

}

