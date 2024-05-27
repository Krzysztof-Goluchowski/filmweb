package repositories

import javax.inject._
import models.Movie
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future

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

  def findMovieById(Id: Int): Future[Option[Movie]] = {
    db.run(movies.filter(_.movieId === Id).result.headOption)
  }

  def updateMovieRating(movieId: Int, stars: Int): Future[Int] = {
    val updateAction = for {
      maybeMovie <- movies.filter(_.movieId === movieId).result.headOption
      updated <- maybeMovie.map { movie =>
        val newNumRatings = movie.numRatings + 1
        val newRating = ((movie.numRatings * movie.averageRating) + stars) / newNumRatings

        val updateQuery = movies.filter(_.movieId === movieId)
          .map(m => (m.averageRating, m.numRatings))
          .update((newRating, newNumRatings))

        updateQuery
      }.getOrElse(DBIO.successful(0))
    } yield updated

    db.run(updateAction)
  }

  def findCategoryByMovieId(Id: Int): Future[Option[String]] = {
    val query = movies.filter(_.movieId === Id).map(_.category).result.headOption
    db.run(query)
  }

  def findMoviesByCategory(category: String, numMoviesToSelect: Int, ratedMovieIds: Seq[Int]): Future[Seq[Movie]] = {
    val idsList = ratedMovieIds.mkString(",")
    val query = sql"""
                     SELECT movie_id, movie_name, average_rating, category, num_ratings, short_description, long_description
                     FROM movies
                     WHERE category = $category AND movie_id NOT IN (#$idsList)
                     ORDER BY RANDOM()
                     LIMIT $numMoviesToSelect
                     """.as[(Int, String, Double, String, Int, Option[String], Option[String])]

    db.run(query).map { movies =>
      movies.map { case (id, name, rating, category, numRatings, shortDesc, longDesc) =>
        Movie(id, name, rating, category, numRatings, shortDesc, longDesc)
      }
    }
  }

  def findRandomMovies(numberOfMovies: Int, excludeMovieIds: Seq[Int]): Future[Seq[Movie]] = {
    val randomFunc = SimpleFunction.nullary[Double]("RANDOM")
    val query = if (excludeMovieIds.nonEmpty) {
      movies.filter(movie => !movie.movieId.inSet(excludeMovieIds)).sortBy(_ => randomFunc).take(numberOfMovies)
    } else {
      movies.sortBy(_ => randomFunc).take(numberOfMovies)
    }
    db.run(query.result)
  }

}
