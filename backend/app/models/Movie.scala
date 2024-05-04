package models

import slick.jdbc.PostgresProfile.api._

case class Movie(movieId: Int, movieName: String, averageRating: Float, category: Category)

class Movies(tag: Tag) extends Table[Movie](tag, "MOVIES") {
  def movieId = column[Int]("MOVIE_ID")
  def movieName = column[String]("MOVIE_NAME")
  def averageRating = column[String]("AVERAGE_RATING")
  def category = column[String]("CATEGORY")
  def * = (movieId, movieName, averageRating, category).mapTo[Movie]
}