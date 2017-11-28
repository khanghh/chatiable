package chatiable.service.facebook

import akka.http.scaladsl.model.HttpMethods
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment.Payload

import scala.concurrent.Future

object FBPageApi {

  def sendTextMessage(
    userId: String,
    message: String
  )(implicit fbHttpClient: FBHttpClient): Future[String] = {
    fbHttpClient.request(
      HttpMethods.POST,
      "me/messages",
      FBSendMessageRequest(
        FBSendMessageRequest.ContactInfo(userId),
        FBSendMessageRequest.Message(Some(message), None)
      )
    )
  }

  def sendAttachment(
    userId: String,
    fileType: String,
    url: String
  )(implicit fbHttpClient: FBHttpClient): Future[String] = {
    fbHttpClient.request(
      HttpMethods.POST,
      "me/messages",
      FBSendMessageRequest(
        FBSendMessageRequest.ContactInfo(userId),
        FBSendMessageRequest.Message(
          None,
          Some(Attachment(fileType, Payload(url)))
        )
      )
    )
  }
}
