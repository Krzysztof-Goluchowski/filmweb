package repositories

import javax.inject._
import models.Movie
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovieRepository @Inject()(implicit ec: ExecutionContext) {
  val db = Database.forConfig("postgres")

  private class MovieTable(tag: Tag) extends Table[Movie](tag, "movies") {
    def movieId = column[Int]("movie_id", O.PrimaryKey, O.AutoInc)
    def movieName = column[String]("movie_name")
    def averageRating = column[Double]("average_rating")
    def category = column[String]("category")
    def numRatings = column[Int]("num_ratings")
    def shortDescription = column[Option[String]]("short_description")
    def longDescription = column[Option[String]]("long_description")

    def * = (movieId, movieName, averageRating, category, numRatings, shortDescription, longDescription) <> ((Movie.apply _).tupled, Movie.unapply)
  }

  private val movies = TableQuery[MovieTable]

  def findMoviesWithCategory(category: String): Future[Seq[Movie]] = {
    db.run(movies.filter(_.category === category).result)
  }

  def findAllMovies(): Future[Seq[Movie]] = {
    db.run(movies.result)
  }

  def findRecommendedMoviesFor(userId: Int): Future[Seq[Movie]] = {
    val query = sql"""
                     SELECT movie_id, movie_name, average_rating, category, num_ratings, short_description, long_description
                     FROM movies
                     ORDER BY RANDOM()
                     LIMIT 10
                     """.as[(Int, String, Double, String, Int, Option[String], Option[String])]

    db.run(query).map { movies =>
      movies.map { case (id, name, rating, category, numRatings, shortDesc, longDesc) =>
        Movie(id, name, rating, category, numRatings, shortDesc, longDesc)
      }
    }
  }

  def findMovieById(Id: Int): Future[Option[Movie]] = {
    db.run(movies.filter(_.movieId === Id).result.headOption)
  }

}
