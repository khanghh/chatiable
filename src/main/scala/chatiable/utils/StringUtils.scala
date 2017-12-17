package chatiable.utils

object StringUtils {
  implicit class RemoveToneMark(str: String) {
    private val convertMap = Map (
      "àáạảãâầấậẩẫăằắặẳẵ" -> "a",
      "ÀÁẠẢÃĂẰẮẶẲẴÂẦẤẬẨẪ" -> "A",
      "èéẹẻẽêềếệểễ" -> "e",
      "ÈÉẸẺẼÊỀẾỆỂỄ" -> "E",
      "òóọỏõôồốộổỗơờớợởỡ" -> "o",
      "ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ" -> "O",
      "ìíịỉĩ" -> "i",
      "ÌÍỊỈĨ" -> "I",
      "ùúụủũưừứựửữ" -> "u",
      "ƯỪỨỰỬỮÙÚỤỦŨ" -> "U",
      "ỳýỵỷỹ" -> "y",
      "ỲÝỴỶỸ" -> "y",
      "Đ" -> "D",
      "đ" -> "d"
    )

    def withoutToneMarks(): String = {
      var result = ""
      str.foreach { character =>
        convertMap.find(tuple => tuple._1.contains(character)) match {
          case Some((key, value)) =>
            result += value
          case None =>
            result += character
        }
      }
      result
    }
  }
}
