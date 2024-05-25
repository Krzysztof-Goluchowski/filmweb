package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import models.Movie
import services.MovieService
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.mvc.Results._
import upickle.default._

@Singleton
class MovieController @Inject()(val controllerComponents: ControllerComponents, movieService: MovieService) extends BaseController {
  val db = Database.forConfig("postgres")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello World!");
  }

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