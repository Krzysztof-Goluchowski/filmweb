//package utils
//
//import play.api.libs.json._
//
//object JsonFormats {
//  implicit val optionStringFormat: Format[Option[String]] = Format(
//    Reads.optionWithNull[String],
//    Writes.optionWithNull[String]
//  )
//}
