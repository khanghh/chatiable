package chatiable.service.facebook.message

import io.circe.Decoder
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

final class FBWebhooksService(

) {
  private var processMessage: IncommingWebhooksMessage => Future[Unit] = {_ => Future()}

  def onReceiveMessage(message: String): Future[_] = {
    decodeMessage[IncommingWebhooksMessage](message).flatMap { message =>
      processMessage(message)
    }
  }

  def setProcessMessage(
    process: IncommingWebhooksMessage => Future[Unit]
  ): Unit = {
    processMessage = process
  }

  private[this] def decodeMessage[B <: FBWebhooksMessage](
    response: String
  )(implicit
    decoder: Decoder[B]
  ): Future[B] = {
    decode[B](response) match {
      case Right(result) => Future(result)
      case Left(ex) =>
//        ex.printStackTrace()
        Future.failed(ex)
    }
  }
}
