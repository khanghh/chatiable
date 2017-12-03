package chatiable.service.user

import chatiable.model.user.MessengerUser
import chatiable.persistence.repository.MessengerUserRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future

final class UserService(
  messengerUserRepository: MessengerUserRepository
) {
  private object Locker
  private val onlineUsers: mutable.ListBuffer[MessengerUser] = mutable.ListBuffer[MessengerUser]()
  private def watingPvPList: mutable.ListBuffer[MessengerUser] = {
    onlineUsers.filter(user => user.friendId.isEmpty)
  }

  def getUser(userId: String): Future[Option[MessengerUser]] = {
    Locker.synchronized {
      onlineUsers.find(_.userId == userId) match {
        case Some(messengerUser) => Future(Some(messengerUser))
        case None =>
          messengerUserRepository.get(userId)
      }
    }
  }

  def addUser(messengerUser: MessengerUser): Future[Unit] = {
    Locker.synchronized {
      onlineUsers += messengerUser
      messengerUserRepository.add(messengerUser)
    }
  }

  def startPair(user: MessengerUser): Unit = {
    Locker.synchronized {
      watingPvPList.find(friend =>
        friend.selectedGender == user.selectedGender &&
          user.selectedGender == friend.selectedGender
      ) match {
        case Some(friend) =>
          user.friendId = friend.userId
          friend.friendId = user.userId
      }
    }
  }

  def stopPair(user: MessengerUser): Unit = {
    Locker.synchronized {
      onlineUsers.find(_.userId == user.friendId) match {
        case Some(friend) =>
          friend.friendId = null
          user.friendId = null
        case None =>

      }
    }
  }

}
