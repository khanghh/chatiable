
package chatiable.service.facebook

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder
import circe._

final case class FBSendMessageRequest(
  recipient: FBSendMessageRequest.ContactInfo,
  message: FBSendMessageRequest.Message
)

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
)

object FBSendMessageRespone {
  implicit val decoder: Decoder[FBSendMessageRespone] = deriveDecoder
}

final case class FBSendMessageException() extends Exception {
  def message: String = "Failed to send messages from Facebook page."
}