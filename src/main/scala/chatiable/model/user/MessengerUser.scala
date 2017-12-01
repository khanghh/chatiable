package chatiable.model.user

final case class MessengerUser(
  userId: String,
  gender: Boolean,
  selectedGender: Boolean
) {
  var lastMgsMilis: Long = 0
}
