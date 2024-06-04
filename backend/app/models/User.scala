package models

import play.api.libs.json.{Json, OFormat}

case class User(userId: Int, firstName: String, lastName: String, login: String, password: String)

object User {
  implicit val format: OFormat[User] = Json.format[User]
}