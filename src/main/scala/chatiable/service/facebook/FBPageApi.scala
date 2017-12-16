package chatiable.service.facebook

import akka.http.scaladsl.model.HttpMethods
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment.Payload
import scala.concurrent.Future

object FBPageApi {

  def sendTextMessage(
    userId: String,
    message: String
  )(implicit fbHttpClient: FBHttpClient): Future[FBSendMessageRespone] = {
    fbHttpClient.request[FBSendMessageRequest, FBSendMessageRespone](
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
  )(implicit fbHttpClient: FBHttpClient): Future[FBSendMessageRespone] = {
    fbHttpClient.request[FBSendMessageRequest, FBSendMessageRespone](
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

  def sendQuickReplies(
    userId: String,
    text: String,
    replies: List[String]
  )(implicit fbHttpClient: FBHttpClient): Future[FBSendQuickRepliesMessageResponse] = {
    fbHttpClient.request[FBSendQuickRepliesMessageRequest, FBSendQuickRepliesMessageResponse](
      HttpMethods.POST,
      "me/messages",
      FBSendQuickRepliesMessageRequest(
        FBSendQuickRepliesMessageRequest.ContactInfo(userId),
        FBSendQuickRepliesMessageRequest.Message(
          text,
          replies.map(FBSendQuickRepliesMessageRequest.Message.QuickReply("text", _, "POSTBACK_PAYLOAD"))
        )
      )
    )
  }

  def getUserInfo(
    userId: String
  )(implicit fbHttpClient: FBHttpClient): Future[FBGetUserInfoResponse] = {
    fbHttpClient.request[FBGetUserInfoResponse](
      HttpMethods.GET,
      userId,
      Map("fields" -> "id,name,gender")
    )
  }

  def sendSenderAction(
    userId: String,
    senderAction: String
  )(implicit fbHttpClient: FBHttpClient): Future[FBSendSenderActionResponse] = {
    fbHttpClient.request[FBSendSenderActionRequest, FBSendSenderActionResponse](
      HttpMethods.POST,
      "me/messages",
      FBSendSenderActionRequest(
        FBSendSenderActionRequest.ContactInfo(userId),
        senderAction
      )
    )
  }

}
