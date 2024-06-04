package models.user

import upickle.default.{ReadWriter}

case class User(userId: Int, firstName: String, lastName: String, login: String, password: String)
    derives ReadWriter

case class Register(firstName: String, lastName: String, login: String, password: String)
    derives ReadWriter

case class Login(login: String, password: String)
    derives ReadWriter