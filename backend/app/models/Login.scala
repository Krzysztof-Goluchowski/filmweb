package models

import play.api.libs.json.{Json, OFormat}

case class Login(login: String, password: String)

object Login {
  implicit val format: OFormat[Login] = Json.format[Login]
}
