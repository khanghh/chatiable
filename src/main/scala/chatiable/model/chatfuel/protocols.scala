
package chatiable.model.chatfuel

import chatiable.model.chatfuel.Messages.Message
import chatiable.model.chatfuel.Messages.Message.Attachment
import chatiable.model.chatfuel.Messages.Message.Attachment.Payload
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder
import chatiable.service.circe._

final case class Messages(
  messages: List[Message]
)

object Messages {

  final case class Message(
    attachment: Option[Attachment],
    text: Option[String]
  )

  object Message {

    final case class Attachment(
      `type`: Option[String],
      payload: Option[Payload]
    )

    object Attachment {

      final case class Payload(
        url: Option[String],
        text: Option[String],
        template_type: Option[String],
        image_aspect_ratio: Option[String],
        top_element_style: Option[String]
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

