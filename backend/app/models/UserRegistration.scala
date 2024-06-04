package models

import play.api.libs.json.{Json, OFormat}

case class UserRegistration(firstName: String, lastName: String, login: String, password: String)

object UserRegistration {
  implicit val format: OFormat[UserRegistration] = Json.format[UserRegistration]
}