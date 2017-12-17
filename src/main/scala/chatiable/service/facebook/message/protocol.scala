package chatiable.service.facebook.message

import java.io.Serializable

import chatiable.service.facebook.message.IncommingWebhooksMessage.Entry.Message.Attachment.ImageAttachment.Payload
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveDecoder
import chatiable.service.facebook.message.circe._

sealed abstract class FBWebhooksMessage

final case class IncommingWebhooksMessage(
  `object`: String,
  entry: Seq[IncommingWebhooksMessage.Entry]
) extends FBWebhooksMessage

object IncommingWebhooksMessage {

  final case class Entry(
    id: String,
    time: Long,
    messaging: Seq[Entry.Messaging]
  )

  object Entry {

    final case class Messaging(
      sender: Messaging.ContactInfo,
      recipient: Messaging.ContactInfo,
      timestamp: Long,
      message: Message
    )

    final case class Message(
      mid: String,
      seq: Long,
      text: Option[String],
      isEcho: Option[Boolean],
      attachments: Option[Seq[Message.Attachment]]
    )

    object Message {

      sealed abstract class Attachment extends Product with Serializable

      object Attachment {

        final case class ImageAttachment(
          `type`: String,
          payload: ImageAttachment.Payload
        ) extends Attachment

        object ImageAttachment {

          final case class Payload(
            url: String
          )

          object Payload {
            implicit val decoder: Decoder[Payload] = deriveDecoder
          }

          implicit val decoder: Decoder[ImageAttachment] = deriveDecoder
        }

        final case class LinkAttachment(
          `type`: String,
          title: String,
          url: String
        ) extends Attachment

        object LinkAttachment {
          implicit val decoder: Decoder[LinkAttachment] = deriveDecoder
        }

        final case class FileAttachment(
          payload: Payload
        ) extends Attachment

        object FileAttachment {
          implicit val decoder: Decoder[FileAttachment] = deriveDecoder
        }

        implicit val decoder: Decoder[Attachment] =
          Decoder.instance { cursor =>
            cursor.downField("type").as[String].flatMap {
              case "fallback" => cursor.as[LinkAttachment]
              case "image" => cursor.as[ImageAttachment]
              case "file" => cursor.as[FileAttachment]
            }
          }
      }

      implicit val decoder: Decoder[Message] = deriveDecoder
    }

    object Messaging {

      final case class ContactInfo(
        id: String
      )

      object ContactInfo {
        implicit val decoder: Decoder[ContactInfo] = deriveDecoder
      }

      implicit val decoder: Decoder[Messaging] = deriveDecoder
    }

    implicit val decoder: Decoder[Entry] = deriveDecoder
  }

  implicit val decoder: Decoder[IncommingWebhooksMessage] = deriveDecoder
}

object FailedToDecodeWebhooksMessage extends Exception
