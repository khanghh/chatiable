package chatiable.model.user.request.pvpchat

import chatiable.model.user.MessengerUser
import chatiable.model.user.request.UserRequest

abstract class PVPChatRequest() extends UserRequest

object PVPChatRequest {
  final case class NewChat() extends PVPChatRequest
  final case class SelectGender() extends PVPChatRequest
  final case class SelectedBoy() extends PVPChatRequest
  final case class SelectedGirl() extends PVPChatRequest
  final case class Pairing(friend: MessengerUser) extends PVPChatRequest
  final case class FindingFriend(selectedGender: Boolean) extends PVPChatRequest
}
