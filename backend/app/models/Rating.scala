package models

import slick.jdbc.PostgresProfile.api._

case class Rating(movieId: Int, userId: Int, stars: Int, review: Option[String]);

class Ratings(tag: Tag) extends Table[Rating](tag, "RATINGS") {
  def movieId = column[Int]("MOVIE_ID")
  def userId = column[Int]("USER_ID")
  def stars = column[Int]("STARS")
  def review = column[String]("REVIEW")
  def * = (movieId, userId, stars, review).mapTo[Rating]
}