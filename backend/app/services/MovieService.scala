package services

import javax.inject._
import repositories.MovieRepository
import repositories.RatingRepository
import models.Movie
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable.Map
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MovieService @Inject()(movieRepository: MovieRepository, ratingRepository: RatingRepository)(implicit ec: ExecutionContext) {

  def getMoviesWithCategory(category: String): Future[Seq[Movie]] = {
    movieRepository.findMoviesWithCategory(category)
  }

  def getMovies: Future[Seq[Movie]] = {
    movieRepository.findAllMovies()
  }


  def getRecommendedMoviesFor(userId: Int): Future[Seq[Movie]] = {
    val highRatedMovies = ratingRepository.getHighRatedMoviesByUserId(userId)
    val categoryCounts = scala.collection.mutable.Map.empty[String, Int]
    var totalRatings = 0

    highRatedMovies.flatMap { ratings =>
      val categoryFutures = ratings.map { rating =>
        movieRepository.findCategoryByMovieId(rating.movieId)
      }

      val allCategoriesFuture = Future.sequence(categoryFutures)
      allCategoriesFuture.flatMap { categories =>
        categories.flatten.foreach { category =>
          val currentCount = categoryCounts.getOrElse(category, 0)
          categoryCounts.update(category, currentCount + 1)
        }

        totalRatings = ratings.size

        movieRepository.findRecommendedMoviesFor(userId).flatMap { movies =>
          categoryCounts.foreach { case (category, count) =>
            println(s"$category: $count")
          }
          println(s"Łącznie ocen użytkownika: $totalRatings")

          val recommendedMoviesFutures = Future.sequence {
            categoryCounts.toSeq.flatMap { case (category, count) =>
              val numMoviesToSelect = ((count.toDouble / totalRatings) * 10).toInt
              val futureMovies = movieRepository.findMoviesByCategory(category, numMoviesToSelect)
              Seq(futureMovies)
            }
          }

          recommendedMoviesFutures.map(_.flatten)
        }
      }
    }
  }


  def getDetailsOfMovie(movieId: Int): Future[Option[Movie]] = {
    movieRepository.findMovieById(movieId)
  }

}
