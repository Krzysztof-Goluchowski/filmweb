package models.movies

import upickle.default.{ReadWriter}

case class MovieDetails(movieId: Int, movieName: String, averageRating: Double, category: String, shortDescription: Option[String] = None, longDescription: Option[String] = None)
    derives ReadWriter

case class Movies(movies: Seq[MovieDetails])
    derives ReadWriter