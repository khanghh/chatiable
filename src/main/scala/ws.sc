
import java.net.URL
import java.time.Instant
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
import chatiable.utils.StringUtils._
import java.time.LocalDate

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat


val pattern = "thời tiết (.+) (hôm nay|ngày mai|ngày .+)$".r

val str = "thời tiết hà nội ngày 1215-1-12"

str match {
  case pattern(param1, param2) =>
    println(param1)
    println(param2)
  case _ => println("none")
}

DateTimeFormat.forPattern("dd/MM/yyyy").withZone(DateTimeZone.getDefault).print(System.currentTimeMillis())