package chatiable.service.user

import chatiable.model.user.MessengerUser

import scala.collection.mutable

object UserManager {

  val onlineUsers: mutable.ListBuffer[MessengerUser] = mutable.ListBuffer[MessengerUser]()

  def getUser(userId: String): Option[MessengerUser] = {
    onlineUsers.synchronized {
      onlineUsers.find(_.userId == userId)
    }
  }

  def addUser(user: MessengerUser): Unit = {
    onlineUsers.synchronized {
      if (!onlineUsers.contains(user)) {
        onlineUsers += user
      }
    }
  }

  def find(p: MessengerUser => Boolean): Option[MessengerUser] = {
    onlineUsers.synchronized {
      onlineUsers.find(p)
    }
  }

  def lock[T](body: => T) = {
    onlineUsers.synchronized {
      body
    }
  }

}
