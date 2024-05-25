package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default._
import javax.inject._
import play.api.mvc._
import services.MovieService

@Singleton
class MovieController @Inject()(val controllerComponents: ControllerComponents, movieService: MovieService) extends BaseController {
  def movies(category: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    category match {
      case Some(cat) =>
        movieService.getMoviesWithCategory(cat).map { movies =>
          Ok(write(movies))
        }
      case None =>
        movieService.getMovies.map { movies =>
          Ok(write(movies))
        }
    }
  }

  def recommended(userId: Int) = Action.async { implicit request: Request[AnyContent] =>
    movieService.getRecommendedMoviesFor(userId).map { movies =>
      Ok(write(movies))
    }
  }

  def details(movieId: Int) = Action.async { implicit request: Request[AnyContent] =>
    movieService.getDetailsOfMovie(movieId).map {
      case Some(movie) => Ok(write(movie))
      case None => NotFound("Movie not found")
    }
  }

}