package chatiable.model.user.request.math

import chatiable.model.user.request.UserRequest

abstract class CocCocMathRequest() extends UserRequest

object CocCocMathRequest {
  final case class NewSolveMath() extends CocCocMathRequest
  final case class UserInputMath() extends CocCocMathRequest
}