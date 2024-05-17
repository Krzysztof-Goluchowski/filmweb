package models

import upickle.default.{ReadWriter}

case class Movie(movieId: Int, movieName: String, averageRating: Double, category: String)
    derives ReadWriter