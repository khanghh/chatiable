package chatiable.persistence.table

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import MessengerUsers._
import chatiable.model.user.MessengerUser

class MessengerUsers(tag: Tag) extends Table[MessengerUser](tag, "MessengerUsers") {
  def id = column[String]("Id", O.PrimaryKey, O.Length(idMaxLength))
  def fullName = column[String]("FullName", O.Length(fullNameMaxLength))
  def gender = column[Boolean]("Gender")
  def lastMsg = column[Long]("LastMessage")
  def `*`: ProvenShape[MessengerUser] = (id, fullName, gender, lastMsg) <> (MessengerUser.tupled, MessengerUser.unapply)
}

object MessengerUsers {
  val idMaxLength = 100
  val fullNameMaxLength = 200
  val query = TableQuery[MessengerUsers]
}