package models

import upickle.default.{ReadWriter, macroRW}
import slick.jdbc.GetResult

case class Movie(
                  movieId: Int,
                  movieName: String,
                  averageRating: Double,
                  category: String,
                  numRatings: Int,
                  shortDescription: Option[String] = None,
                  longDescription: Option[String] = None
                ) derives ReadWriter

object Movie {
  implicit val getMovieResult: GetResult[Movie] = GetResult(r =>
    Movie(
      r.<<, // movieId
      r.<<, // movieName
      r.<<, // averageRating
      r.<<, // category
      r.<<, // numRatings
      r.<<?[String], // shortDescription
      r.<<?[String] // longDescription
    )
  )
}
