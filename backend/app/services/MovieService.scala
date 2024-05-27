package services

import javax.inject._
import repositories.MovieRepository
import repositories.RatingRepository
import models.Movie
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable.Map
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

@Singleton
class MovieService @Inject()(movieRepository: MovieRepository, ratingRepository: RatingRepository)(implicit ec: ExecutionContext) {

  def getMoviesWithCategory(category: String): Future[Seq[Movie]] = {
    movieRepository.findMoviesWithCategory(category)
  }

  def getMovies: Future[Seq[Movie]] = {
    movieRepository.findAllMovies()
  }


  def getRecommendedMoviesFor(userId: Int): Future[Seq[Movie]] = {
    val highRatedMoviesFuture = ratingRepository.findHighRatedMoviesByUserId(userId)
    val categoryCounts = scala.collection.mutable.Map.empty[String, Int]

    highRatedMoviesFuture.flatMap { ratings =>
      val ratedMovieIds = ratings.map(_.movieId)

      val categoryFutures = ratings.map { rating =>
        movieRepository.findCategoryByMovieId(rating.movieId)
      }

      val allCategoriesFuture = Future.sequence(categoryFutures)
      allCategoriesFuture.flatMap { categories =>
        categories.flatten.foreach { category =>
          val currentCount = categoryCounts.getOrElse(category, 0)
          categoryCounts.update(category, currentCount + 1)
        }

        val totalRatings = ratings.size

        val recommendedMoviesFutures = Future.sequence {
          categoryCounts.toSeq.flatMap { case (category, count) =>
            val numMoviesToSelect = ((count.toDouble / totalRatings) * 10).toInt
            val futureMovies = movieRepository.findMoviesByCategory(category, numMoviesToSelect, ratedMovieIds)
            Seq(futureMovies)
          }
        }

        recommendedMoviesFutures.flatMap { movies =>
          val flattenedMovies = movies.flatten
          val flattenedMovieIds = flattenedMovies.map(_.movieId)
          val numberOfMovies = flattenedMovies.size

          if (numberOfMovies < 10) {
            val additionalMoviesNeeded = 10 - numberOfMovies
            movieRepository.findRandomMovies(additionalMoviesNeeded, ratedMovieIds ++ flattenedMovieIds).map { randomMovies =>
              Random.shuffle(flattenedMovies ++ randomMovies)
            }
          } else {
            Future.successful(Random.shuffle(flattenedMovies))
          }
        }
      }
    }
  }


  def getDetailsOfMovie(movieId: Int): Future[Option[Movie]] = {
    movieRepository.findMovieById(movieId)
  }

}
