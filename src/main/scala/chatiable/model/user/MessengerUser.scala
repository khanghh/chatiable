package chatiable.model.user

import chatiable.model.user.request.UserRequest

final case class MessengerUser(
  userId: String,
  name: String,
  gender: Boolean,
  var lastMgsMilis: Long = 0
) {

  var request: UserRequest = _
}
