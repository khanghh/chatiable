package chatiable.service.chatfuel

import chatiable.service.chatfuel.Messages.Message
import chatiable.service.chatfuel.Messages.Message.Attachment
import chatiable.service.chatfuel.Messages.Message.Attachment.Payload
import io.circe.Printer

final class ChatfuelService() {
  def sendTextMessage(text: String): String = {
    ChatfuelApi.sendTextMessage(text)
  }

  def sendSilent: String = {
    ChatfuelApi.sendSilent
  }

  def sendAttachment(url: String, chatfuelType: String): String = {
    ChatfuelApi.sendAttachment(url, chatfuelType)
  }

  def sendMessages(messages: Message*): String = {
    ChatfuelApi.sendMessages(messages)
  }

  def redirect(blockName: String): String = {
    ChatfuelApi.redirect(blockName)
  }
}
