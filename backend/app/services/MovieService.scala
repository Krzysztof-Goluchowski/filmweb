package services

import javax.inject._
import repositories.MovieRepository
import models.Movie
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovieService @Inject()(movieRepository: MovieRepository)(implicit ec: ExecutionContext) {

  def getMoviesWithCategory(category: String): Future[Seq[Movie]] = {
    movieRepository.findMoviesWithCategory(category)
  }

  def getMovies: Future[Seq[Movie]] = {
    movieRepository.findAllMovies()
  }

  def getRecommendedMoviesFor(userId: Int): Future[Seq[Movie]] = {
    movieRepository.findRecommendedMoviesFor(userId)
  }

  def getDetailsOfMovie(movieId: Int): Future[Option[Movie]] = {
    movieRepository.findMovieById(movieId)
  }

}
