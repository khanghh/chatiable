
package chatiable.service.facebook

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.extras.semiauto.deriveEncoder
import io.circe.generic.extras.semiauto.deriveDecoder
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

final case class FBSendQuickRepliesMessageRequest(
  recipient: FBSendQuickRepliesMessageRequest.ContactInfo,
  message:  FBSendQuickRepliesMessageRequest.Message
) extends FBRequest

object FBSendQuickRepliesMessageRequest {

  final case class ContactInfo(
    id: String
  )

  object ContactInfo {
    implicit val encoder: Encoder[ContactInfo] = deriveEncoder
    implicit val decoder: Decoder[ContactInfo] = deriveDecoder
  }

  final case class Message(
    text: String,
    quickReplies: List[Message.QuickReply]
  )

  object Message {

    final case class QuickReply(
      contentType: String,
      title: String,
      payload: String
    )

    object QuickReply {
      implicit val encoder: Encoder[QuickReply] = deriveEncoder
      implicit val decoder: Decoder[QuickReply] = deriveDecoder
    }

    implicit val encoder: Encoder[Message] = deriveEncoder
    implicit val decoder: Decoder[Message] = deriveDecoder
  }

  implicit val encoder: Encoder[FBSendQuickRepliesMessageRequest] = deriveEncoder
  implicit val decoder: Decoder[FBSendQuickRepliesMessageRequest] = deriveDecoder
}

final case class FBSendQuickRepliesMessageResponse(
  recipientId: String,
  messageId: String,
) extends FBResponse

object FBSendQuickRepliesMessageResponse {
  implicit val decoder: Decoder[FBSendQuickRepliesMessageResponse] = deriveDecoder
}

final case class FBSendSenderActionRequest(
  recipient: FBSendSenderActionRequest.ContactInfo,
  senderAction: String
) extends FBRequest

object FBSendSenderActionRequest {

  final case class ContactInfo(
    id: String
  )

  object ContactInfo {
    implicit val encoder: Encoder[ContactInfo] = deriveEncoder
  }

  implicit val encoder: Encoder[FBSendSenderActionRequest] = deriveEncoder
}

object SenderActions {
  val MarkSeen = "mark_seen"
  val TypingOn = "typing_on"
  val TypingOff = "typing_off"
}

final case class FBSendSenderActionResponse(
  recipientId: String
) extends FBResponse

object FBSendSenderActionResponse {
  implicit val decoder: Decoder[FBSendSenderActionResponse] = deriveDecoder
}