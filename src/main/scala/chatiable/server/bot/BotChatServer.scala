package chatiable.server.bot

import akka.actor.Actor
import chatiable.persistence.repository.BotActionRepository
import chatiable.persistence.repository.BotReplyRepository
import chatiable.persistence.table.BotReplies
import chatiable.server.bot.BotChatServer.CreateTableIfNotExist
import chatiable.server.bot.BotChatServer.HandleMessage
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Random
import scala.util.Try

class BotChatServer(
  database: Database
) extends Actor {
  val botReplyRepo: BotReplyRepository = new BotReplyRepository(database)
  val botActionRepo: BotActionRepository = new BotActionRepository(database)
  val random = new Random(System.currentTimeMillis())

  override def receive: Receive = {
    case HandleMessage(userId, message) =>
//      replyMessage(userId, message)
    case CreateTableIfNotExist =>
      Await.result(database.run(DBIO.seq(
        BotReplies.query.schema.create
      )).recoverWith {
        case exception: Exception => Future.failed(exception)
      }, Duration.Inf)
  }
//
//  def replyMessage(userId: String, message: String): Unit = {
//    message match {
//      case teachCmd if (message.startsWith("#day ")) =>
//        val teach = teachCmd.replace("#day ", "").split('|')
//        if (teach.length == 3) {
//          botReplyRepo.add(
//            teach.apply(0),
//            teach.apply(1),
//            Try(teach.apply(2).toInt).getOrElse(0)
//          )
//          FBPageApi.sendTextMessage(userId, s"${teach.apply(0)} => ${teach.apply(1)}")
//        }
//      case ask =>
//        botReplyRepo.get(message).map(replies => {
//          var rnd = random.nextInt(replies.map(_.probabl).sum + 1)
//          replies.find(reply => {
//            rnd = rnd - reply.probabl
//            rnd <= 0
//          }) match {
//            case Some(botReply) =>
//              FBPageApi.sendTextMessage(userId, botReply.reply)
//            case None =>
//              replyMessageDefault(userId, message)
//          }
//        })
//    }
//  }
//
//  def replyAction(userId: String, message: String): Unit = {
//    botActionRepo.get.map(actions => {
//      actions.find(action => {
//        val pattern = action.askPattern.r
//        message match {
//          case pattern =>
//
//            true
//          case pattern(param1) =>
//            true
//          case pattern(param1, param2) =>
//            true
//          case _ =>
//            true
//        }
//      })
//    })
//  }
//
//  def doAction() = {
//
//  }
//
//  def replyMessageDefault(userId: String, message: String): Unit = {
//    botReplyRepo.get("*").map(replies => {
//      var rnd = random.nextInt(replies.map(_.probabl).sum + 1)
//      replies.find(reply => {
//        rnd = rnd - reply.probabl
//        rnd <= 0
//      }) match {
//        case Some(botReply) =>
//          FBPageApi.sendTextMessage(userId, botReply.reply)
//        case None =>
//      }
//    })
//  }

}

object BotChatServer {
  final case object CreateTableIfNotExist
  final case class HandleMessage(userId: String, message: String)
  final case class UserEndChat(userId: String)
}