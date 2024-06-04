package services

import javax.inject._
import repositories.MovieRepository
import repositories.RatingRepository
import models.Movie
import scala.collection.mutable.Map
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

@Singleton
class MovieService @Inject()(movieRepository: MovieRepository, ratingRepository: RatingRepository) {

  def getCategories(): Future[Seq[String]] = {
    movieRepository.findCategories()
  }

  def getMoviesWithCategory(category: String): Future[Seq[Movie]] = {
    movieRepository.findMoviesWithCategory(category)
  }

  def getMovies: Future[Seq[Movie]] = {
    movieRepository.findAllMovies()
  }

  def getRecommendedMoviesFor(userId: Int): Future[Seq[Movie]] = {
    for {
      highRatedMovies <- ratingRepository.findHighRatedMoviesByUserId(userId)
      categories <- getCategoriesForMovies(highRatedMovies.map(_.movieId))
      categoryCounts = countMoviesByCategory(categories)
      recommendedMovies <- getRecommendedMoviesByCategory(categoryCounts, highRatedMovies.map(_.movieId))
      finalMovies <- ensureMinimumMovies(recommendedMovies, highRatedMovies.map(_.movieId), 10)
    } yield finalMovies
  }

  private def getCategoriesForMovies(movieIds: Seq[Int]): Future[Seq[String]] = {
    val categoryFutures = movieIds.map(movieRepository.findCategoryByMovieId)
    Future.sequence(categoryFutures).map(_.flatten)
  }

  private def countMoviesByCategory(categories: Seq[String]): Map[String, Int] = {
    val categoryCounts = Map.empty[String, Int]
    categories.foreach { category =>
      val currentCount = categoryCounts.getOrElse(category, 0)
      categoryCounts.update(category, currentCount + 1)
    }
    categoryCounts
  }

  private def getRecommendedMoviesByCategory(categoryCounts: Map[String, Int], ratedMovieIds: Seq[Int]): Future[Seq[Movie]] = {
    val totalRatings = ratedMovieIds.size
    val recommendedMoviesFutures = Future.sequence {
      categoryCounts.toSeq.flatMap { case (category, count) =>
        val numMoviesToSelect = ((count.toDouble / totalRatings) * 10).toInt
        val futureMovies = movieRepository.findMoviesByCategory(category, numMoviesToSelect, ratedMovieIds)
        Seq(futureMovies)
      }
    }
    recommendedMoviesFutures.map(_.flatten)
  }

  private def ensureMinimumMovies(movies: Seq[Movie], ratedMovieIds: Seq[Int], minimum: Int): Future[Seq[Movie]] = {
    val numberOfMovies = movies.size
    if (numberOfMovies < minimum) {
      val additionalMoviesNeeded = minimum - numberOfMovies
      movieRepository.findRandomMovies(additionalMoviesNeeded, ratedMovieIds ++ movies.map(_.movieId)).map { randomMovies =>
        Random.shuffle(movies ++ randomMovies)
      }
    } else {
      Future.successful(Random.shuffle(movies))
    }
  }

  def getDetailsOfMovie(movieId: Int): Future[Option[Movie]] = {
    movieRepository.findMovieById(movieId)
  }

}
