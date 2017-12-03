package chatiable.service.user
import chatiable.model.user.MessengerUser

import scala.collection.mutable

final class PVPChatService {
  val watingUsers: mutable.ListBuffer[MessengerUser] = mutable.ListBuffer[MessengerUser]()

  private object Locker

  def startPair(user: MessengerUser): Unit = {
    Locker.synchronized {
      watingUsers.find(friend =>
        friend.selectedGender == user.selectedGender &&
          user.selectedGender == friend.selectedGender
      ) match {
        case Some(friend) =>
          user.friendId = friend.userId
          friend.friendId = user.userId
          watingUsers -= friend
        case None =>
          watingUsers += user
      }
    }
  }

  def stopPair(user: MessengerUser): Unit = {
    Locker.synchronized {

    }
  }

}
