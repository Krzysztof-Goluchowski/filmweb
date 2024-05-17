package models

import upickle.default.{ReadWriter}

case class User(userId: Int, firstName: String, lastName: String, login: String, password: String)
    derives ReadWriter