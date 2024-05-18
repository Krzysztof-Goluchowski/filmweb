package models

import upickle.default.{ReadWriter}

case class Login(login: String, password: String)
    derives ReadWriter