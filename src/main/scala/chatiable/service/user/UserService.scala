package chatiable.service.user

import chatiable.model.user.MessengerUser
import chatiable.persistence.repository.MessengerUserRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future

final class UserService(
  messengerUserRepo: MessengerUserRepository
) {

  def getUser(userId: String): Future[Option[MessengerUser]] = {
    Future(UserManager.getUser(userId))
  }

  def addUser(user: MessengerUser): Future[_] = {
    UserManager.addUser(user)
    messengerUserRepo.insertOrUpdate(user)
  }

  def updateUser(user: MessengerUser): Future[_] = {
    messengerUserRepo.update(user)
  }

}
