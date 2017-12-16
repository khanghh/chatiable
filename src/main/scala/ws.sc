
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import chatiable.model.user.MessengerUser
import chatiable.model.user.request.pvpchat.PVPChatRequest
import chatiable.service.facebook.FBSendQuickRepliesMessageRequest
import io.circe.generic.extras.Configuration

import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Failure

val list  = List()

val thread1 = new Thread {
  override def run: Unit = {
    list.synchronized {
      Thread.sleep(5000)
      println("1")
    }
  }
}

val thread2 = new Thread {
  override def run: Unit = {
    list.synchronized {
      println("2")
    }
  }
}

thread1.start()
thread2.start()
