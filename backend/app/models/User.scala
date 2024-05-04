package models

import slick.jdbc.PostgresProfile.api._
import play.api.libs.json._
import slick.jdbc.GetResult

case class User(userId: Int, firstName: String, lastName: String, login: String, password: String);

object User {
  implicit val getUserResult: GetResult[User] = GetResult(r => User(r.nextInt(), r.nextString(), r.nextString(), r.nextString(), r.nextString()))
  implicit val userFormat: OFormat[User] = Json.format[User]
}

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def userId = column[Int]("USER_ID")
  def firstName = column[String]("FIRSTNAME")
  def lastName = column[String]("LASTNAME")
  def login = column[String]("LOGIN")
  def password = column[String]("PASSWORD")
  def * = (userId, firstName, lastName, login, password).mapTo[User]
}