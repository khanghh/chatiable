
package chatiable.service.facebook

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder
import circe._

abstract class FBRequest
abstract class FBResponse

final case class FBSendMessageRequest(
  recipient: FBSendMessageRequest.ContactInfo,
  message: FBSendMessageRequest.Message
) extends FBRequest

object FBSendMessageRequest {

  final case class ContactInfo(
    id: String
  )

  object ContactInfo {
    implicit val encoder: Encoder[ContactInfo] = deriveEncoder
  }

  final case class Message(
    text: Option[String],
    attachment: Option[Message.Attachment]
  )

  object Message {

    final case class Attachment(
      `type`: String,
      payload: Attachment.Payload
    )

    object Attachment {

      final case class Payload(
        url: String
      )

      object Payload {
        implicit val encoder: Encoder[Payload] = deriveEncoder
      }

      implicit val encoder: Encoder[Attachment] = deriveEncoder
    }

    implicit val encoder: Encoder[Message] = deriveEncoder
  }

  implicit val encoder: Encoder[FBSendMessageRequest] = deriveEncoder
}

final case class FBSendMessageRespone(
  recipientId: String,
  messageId: String,
  attachmentId: Option[String]
) extends FBResponse

object FBSendMessageRespone {
  implicit val decoder: Decoder[FBSendMessageRespone] = deriveDecoder
}

final case class FBSendMessageException() extends Exception {
  def message: String = "Failed to send messages from Facebook page."
}

final case class FBGetUserInfoResponse(
  id: String,
  name: String,
  gender: String
) extends FBResponse

object FBGetUserInfoResponse {
  implicit val decoder: Decoder[FBGetUserInfoResponse] = deriveDecoder
}