package chatiable.service.math

import chatiable.service.math.CCSendSolveMathResponse.Math.Variant
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant.Answer
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant.Solution
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant.Solution.Step
import chatiable.service.math.CCSendSolveMathResponse.Math.Variant.TexImage
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveDecoder
import circe._

abstract class CCRequest
abstract class CCResponse

final case class CCSendSolveMathResponse(
  math: CCSendSolveMathResponse.Math,
) extends CCResponse

object CCSendSolveMathResponse {

  final case class Math(
    date: Option[String],
    info: Option[String],
    variants: Option[List[Variant]],
//    error: Option[String],
//    result: Option[Boolean]
  )

  object Math {

    final case class Variant(
      answers: List[Answer],
      currencyFlag: Boolean,
      solution: List[Solution],
      texImage: TexImage,
      texUrl: String
    )

    object Variant {

      final case class Answer(
        answer_url: String,
        description: String,
        replaced_formula: String
      )

      object Answer {

        implicit val decoder: Decoder[Answer] = deriveDecoder
      }

      final case class Solution(
        description: Option[String],
        domain_answer: Option[List[String]],
        steps: List[Step]
      )

      object Solution {

        final case class Step(
          img_url: String,
          text: String
        )

        object Step {

          implicit val decoder: Decoder[Step] = deriveDecoder
        }

        implicit val decoder: Decoder[Solution] = deriveDecoder
      }

      final case class TexImage(
        replaced_formula: String
      )

      object TexImage {

        implicit val decoder: Decoder[TexImage] = deriveDecoder
      }

      implicit val decoder: Decoder[Variant] = deriveDecoder
    }

    implicit val decoder: Decoder[Math] = deriveDecoder
  }

  implicit val decoder: Decoder[CCSendSolveMathResponse] = deriveDecoder
}
