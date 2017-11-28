
import java.net.URL

import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Failure

val map1 = Map(1 -> "1", 2 -> "2", 3 -> "2")

map1.find(p => p._2 == "2")

val str = "http://dgsg.com/affdbdfbdfb?dfbdfb=dfbdb?dfbdfb.pag"

str.matches("\\S+")

str match {
  case longUrl if (str.matches("\\S+")) =>
    longUrl.split('?').apply(0).split('.').lastOption match {
      case Some(ext) =>
        ext match {
          case "png" | "jpg" => println(ext)
          case _ =>
        }
      case None => println(longUrl)
    }
  case _ =>
}