package chatiable.service.math

import akka.http.scaladsl.HttpExt
import akka.stream.ActorMaterializer
import chatiable.model.user.MessengerUser
import chatiable.model.user.request.math.CocCocMathRequest.NewSolveMath
import chatiable.model.user.request.math.CocCocMathRequest.UserInputMath
import chatiable.server.ChatiableServerConfig
import chatiable.service.facebook.FBHttpClient
import chatiable.service.facebook.FBPageApi
import chatiable.service.facebook.SenderActions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class CCMathService(
)(implicit
  materializer: ActorMaterializer,
  http: HttpExt
) {
  def handleMessage(user: MessengerUser, message: String): Future[_] = {
    implicit val ccHttpClient: CCHttpClient = new CCHttpClient()
    implicit val fbHttpClient: FBHttpClient = new FBHttpClient(ChatiableServerConfig.fbAccessToken)
    user.request match {
      case NewSolveMath() =>
        user.request = UserInputMath()
        FBPageApi.sendTextMessage(user.userId, "Nhập biểu thức cần tính:")
      case UserInputMath() =>
        CocCocMathApi.getMathResult(message).flatMap { response =>
          response.math.variants match {
            case Some(variants) =>
              for {
                _ <- FBPageApi.sendTextMessage(user.userId, s"Bài toán: ${variants.head.texImage.replaced_formula}")
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
                _ <- FBPageApi.sendAttachment(user.userId, "image", variants.head.texUrl)
                _ <- FBPageApi.sendTextMessage(user.userId, s"Kết quả: ${variants.head.answers.head.replaced_formula}")
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn)
                _ <- FBPageApi.sendAttachment(user.userId, "image", variants.head.answers.head.answer_url)
              } yield user.request = null
            case None =>
              for {
                _ <- FBPageApi.sendSenderAction(user.userId, SenderActions.TypingOn).map(_ => Thread.sleep(500))
                _ <- FBPageApi.sendTextMessage(user.userId, "Truy vấn của bạn không phải là toán học. Vui lòng nhập đúng truy vấn toán học!")
              } yield user.request = null
          }
        }
    }
  }
}
