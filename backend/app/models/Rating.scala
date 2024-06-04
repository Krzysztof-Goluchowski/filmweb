package models

import play.api.libs.json.{Json, OFormat}

case class Rating(movieId: Int, userId: Int, stars: Int, review: String)

object Rating {
  implicit val format: OFormat[Rating] = Json.format[Rating]
}