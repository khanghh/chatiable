package chatiable.model

final case class MessengerUser(
  userId: String,
  firstName: String,
  lastName: String,
  gender: Boolean,
) {
  var chatFriend: MessengerUser = null
  var selectedGender: Boolean = true
}