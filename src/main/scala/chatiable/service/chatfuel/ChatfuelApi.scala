package chatiable.service.chatfuel

import chatiable.service.chatfuel.Messages.Message
import chatiable.service.chatfuel.Messages.Message.Attachment
import chatiable.service.chatfuel.Messages.Message.Attachment.Payload
import io.circe.Printer

object ChatfuelApi {

  def sendTextMessage(text: String): String = {
    Printer.noSpaces.copy(dropNullKeys = true).pretty(
      Messages.encoder.apply(
        Messages(List(
          Message(text = Some(text))
        ))
      )
    )
  }

  def sendSilent: String = {
    Printer.noSpaces.copy(dropNullKeys = true).pretty(
      Messages.encoder.apply(
        Messages(List(
          Message(None)
        ))
      )
    )
  }

  def sendAttachment(url: String, chatfuelType: String): String = {
    Printer.noSpaces.copy(dropNullKeys = true).pretty(
      Messages.encoder.apply(
        Messages(List(
          Message(
            text = None,
            attachment = Some(Attachment(
              `type` = chatfuelType,
              payload = Payload(url)
            ))
          )
        ))
      )
    )
  }

  def sendMessages(messages: Seq[Message]): String = {
    Printer.noSpaces.copy(dropNullKeys = true).pretty(
      Messages.encoder.apply(
        Messages(messages.toList)
      )
    )
  }

  def redirect(blockName: String): String = {
    Redirect.encoder.apply(
      Redirect(List(blockName))
    ).noSpaces
  }
}