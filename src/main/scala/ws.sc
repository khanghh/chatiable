
import java.net.URL

import chatiable.model.bot.BotReply

import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Failure
import scala.collection.mutable
import scala.util.Random


val str = "thời tiết Hà nội như thế nào ?"

val pattern = """thời tiết (.+) như thế nào \?""".r

str match {
  case pattern(place, time) =>
    println(place)
    println(time)
  case pattern(place) =>
    println(place)
  case _ =>
}

val random = new Random(System.currentTimeMillis())
val botReply = BotReply("ask", "rep", 100)

val replies: List[BotReply] = List()
try {
  val sum = replies.map(rep => rep.probabl).sum
  var rnd = random.nextInt(0)
} catch {
  case ex:Throwable => println(ex)
}

