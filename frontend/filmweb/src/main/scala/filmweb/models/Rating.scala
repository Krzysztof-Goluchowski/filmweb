package models.rating

import upickle.default.{ReadWriter}

case class Rating(movieId: Int, userId: Int, stars: Int, review: Option[String])
    derives ReadWriter