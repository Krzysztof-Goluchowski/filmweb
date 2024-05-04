package models

import slick.jdbc.PostgresProfile.api._

case class User(userId: Int, firstName: String, lastName: String, login: String, password: String);

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def userId = column[Int]("USER_ID")
  def firstName = column[String]("FIRSTNAME")
  def lastName = column[String]("LASTNAME")
  def login = column[String]("LOGIN")
  def password = column[String]("PASSWORD")
  def * = (userId, firstName, lastName, login, password).mapTo[User]
}