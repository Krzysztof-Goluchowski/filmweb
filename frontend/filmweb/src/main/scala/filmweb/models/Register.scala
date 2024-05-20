package models

import upickle.default.{ReadWriter}

case class Register(firstName: String, lastName: String, login: String, password: String)
    derives ReadWriter