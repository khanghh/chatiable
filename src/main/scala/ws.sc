
import java.net.URL


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

val lst = List("1", "2", "3")

lst.find(x => x == "1") match {
  case None => Some(0)
  case _ =>
}

