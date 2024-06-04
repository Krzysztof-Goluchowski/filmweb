package models.responses

import upickle.default.{ReadWriter}

case class SuccessResponse(message: String)
    derives ReadWriter

case class ErrorResponse(message: String)

case class LoginResponse(userId: Int)
    derives ReadWriter