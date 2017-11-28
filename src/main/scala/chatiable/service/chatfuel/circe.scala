package chatiable.service.chatfuel

import io.circe.generic.extras.Configuration

object circe {
  implicit val config = Configuration.default.withSnakeCaseKeys
}
