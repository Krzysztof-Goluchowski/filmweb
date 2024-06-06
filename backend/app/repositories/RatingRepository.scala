package repositories

import javax.inject._
import models.Rating
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import repositories.MovieRepository

@Singleton
class RatingRepository @Inject(movieRepository: MovieRepository)(implicit ec: ExecutionContext) {
  val db = Database.forConfig("postgres")

  private class RatingsTable(tag: Tag) extends Table[Rating](tag, "ratings") {
    def movieId = column[Int]("movie_id")

    def userId = column[Int]("user_id")

    def stars = column[Int]("stars")

    def review = column[String]("review")

    def * = (movieId, userId, stars, review) <> ((Rating.apply _).tupled, Rating.unapply)
  }

  private val ratings = TableQuery[RatingsTable]

  def findRatings(userId: Int): Future[Seq[Rating]] = {
    db.run(ratings.filter(r => r.userId === userId).result)
  }

  def hasAlreadyGivenRating(userId: Int, movieId: Int): Future[Boolean] = {
    val query = ratings.filter(r => r.userId === userId && r.movieId === movieId).exists.result
    db.run(query)
  }

  def createRating(movieId: Int, userId: Int, stars: Int, review: String): Future[Int] = {
    val insertQuery = ratings += Rating(movieId, userId, stars, review)
    db.run(insertQuery)
    movieRepository.updateMovieRating(movieId, stars)
  }

  def findHighRatedMoviesByUserId(userId: Int): Future[Seq[Rating]] = {
    db.run(ratings.filter(r => r.userId === userId && r.stars >= 4).result)
  }
}
