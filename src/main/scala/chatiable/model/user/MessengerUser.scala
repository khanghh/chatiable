package chatiable.model.user

final case class MessengerUser(
  userId: String,
  name: String,
  gender: Boolean,
  var friendId: String = null,
  var lastMgsMilis: Long = 0
) {
  var selectedGender: Boolean = false
}
