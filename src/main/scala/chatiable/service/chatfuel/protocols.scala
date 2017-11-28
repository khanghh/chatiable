
package chatiable.service.chatfuel

import chatiable.service.chatfuel.Messages.Message
import chatiable.service.chatfuel.Messages.Message.Attachment
import chatiable.service.chatfuel.Messages.Message.Attachment.Payload
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import circe._

final case class Messages(
  messages: List[Message]
)

object Messages {

  final case class Message(
    text: Option[String],
    attachment: Option[Attachment] = None
  )

  object Message {

    final case class Attachment(
      `type`: String,
      payload: Payload
    )

    object Attachment {

      final case class Payload(
        url: String,
      )

      object Payload {
        implicit val encoder: Encoder[Payload] = deriveEncoder
      }

      implicit val encoder: Encoder[Attachment] = deriveEncoder
    }

    implicit val encoder: Encoder[Message] = deriveEncoder
  }

  implicit val encoder: Encoder[Messages] = deriveEncoder
}

final case class Redirect(
  redirect_to_blocks: List[String]
)

object Redirect {
  implicit val encoder: Encoder[Redirect] = deriveEncoder
}

object ChatfuelType {
  val Image: String = "image"
  val Audio: String = "audio"
  val Video: String = "video"
  val File: String = "file"
}