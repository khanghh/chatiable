package chatiable.service

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.HttpMethods
import akka.stream.ActorMaterializer
import chatiable.service.facebook.FBGetUserInfoResponse
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.FBSendMessageRequest
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment
import chatiable.service.facebook.FBSendMessageRequest.Message.Attachment.Payload
import chatiable.service.facebook.FBSendMessageRespone

import scala.concurrent.Future

final class FBPageService(
  accessToken: String,
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {

  implicit val fbHttpClient = new FBHttpClient(accessToken)

  def sendTextMessage(
    userId: String,
    message: String
  ): Future[FBSendMessageRespone] = {
    FBPageApi.sendTextMessage(userId, message)
  }

  def sendAttachment(
    userId: String,
    fileType: String,
    url: String
  ): Future[FBSendMessageRespone] = {
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

  def getUserInfo(
    userId: String
  ): Future[FBGetUserInfoResponse] = {
    fbHttpClient.request[FBGetUserInfoResponse](
      HttpMethods.GET,
      userId,
      Map("fields" -> "id,name,gender")
    )
  }
}
