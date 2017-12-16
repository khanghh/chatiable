package chatiable.service.chatfuel

import chatiable.service.chatfuel.Messages.Message

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
