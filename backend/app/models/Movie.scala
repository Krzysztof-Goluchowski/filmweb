package models

import upickle.default.{ReadWriter}

case class Movie(movieId: Int, movieName: String, averageRating: Double, category: String, shortDescription: Option[String] = None, longDescription: Option[String] = None)
    derives ReadWriter