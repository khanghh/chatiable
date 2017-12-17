
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
import chatiable.utils.StringUtils._


val str = "ế ấ ụ"

str.withoutToneMarks()