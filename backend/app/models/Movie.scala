package models

import upickle.default.{ReadWriter, macroRW}

case class Movie(
                  movieId: Int,
                  movieName: String,
                  averageRating: Double,
                  category: String,
                  numRatings: Int,
                  shortDescription: Option[String] = None,
                  longDescription: Option[String] = None
                ) derives ReadWriter
