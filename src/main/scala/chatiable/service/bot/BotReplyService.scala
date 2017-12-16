package chatiable.service.bot

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.DateTime
import akka.stream.ActorMaterializer
import chatiable.model.bot.BotReply
import chatiable.model.user.MessengerUser
import chatiable.persistence.repository.BotReplyRepository
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.SenderActions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scala.util.Try

final class BotReplyService(
  botReplyRepo: BotReplyRepository
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {
  val ramdom = new Random(System.currentTimeMillis())

  def parsePlaceHolder(user: MessengerUser, reply: String): String = {
    DateTime
    reply
      .replaceAll(
        "\\$pronoun",
        if (user.gender) "anh" else "chị"
      )
      .replaceAll(
        "\\$datetime",
        LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
      )
      .replaceAll(
        "\\$date",
        LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
      )
      .replaceAll(
        "\\$time",
        LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("hh:mm:ss"))
      )
  }

  def handleMessage(user: MessengerUser, message: String): Future[Unit] = {
    implicit val fbHttpClient = new FBHttpClient(ChatiableServerConfig.accessToken)
    val teachPattern = """#addrep (.*)\|(.*)\|(.*)$""".r
    message match {
      case teachPattern(ask, rep, prob) =>
        for {
          _ <- botReplyRepo.add(ask, rep, Try(prob.toInt).getOrElse(0))
          _ <- FBPageApi.sendTextMessage(user.userId, s"$ask => $rep")
        } yield ()
      case _ =>
        for {
          replies <- botReplyRepo.get(message)
          defaultReps <- botReplyRepo.get("*")
        } yield {
          getRandomBotReply(replies).orElse(
            getRandomBotReply(defaultReps)
          ) match {
            case Some(botReply) =>
              val replyMsg = parsePlaceHolder(user, botReply.reply)
              for {
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn).map(_ => Thread.sleep(500))
                _ <- FBPageApi.sendTextMessage(user.userId, replyMsg)
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOff)
              } yield {
                user.request = null
              }
            case None =>
              FBPageApi.sendTextMessage(user.userId, "không có dữ liệu")
                .map(_ => user.request = null)
          }
        }
    }
  }

  def getRandomBotReply(replies: Seq[BotReply]): Option[BotReply] = {
    var rnd = ramdom.nextInt(replies.map(_.probabl).sum + 1)
    replies.find(reply => {
      rnd = rnd - reply.probabl
      rnd <= 0
    })
  }
}
