package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val db = Database.forConfig("h2mem1")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello world");
  }

  def movies(category: Option[String]) = Action { implicit request: Request[AnyContent] =>
    Ok("filtered movies from category " + category);
  }

  def recommended(userId: Int) = Action {implicit request: Request[Any] =>
    Ok("recommended");
  }

  def details(movieId: Int) = Action {implicit request: Request[Any] =>
    Ok("details of movie with id " + movieId);
  }

  def rate() = Action {implicit request: Request[AnyContent] =>
    val requestBodyJson = request.body.asJson
    requestBodyJson.map { json =>
      val movieId = (json \ "movieId").as[Int]
      val userId = (json \ "userId").as[Int]
      val stars = (json \ "stars").as[Int]
      Ok(s"Received rating ($stars stars) from user $userId for movie: $movieId")
    }.getOrElse {
      BadRequest("Expecting application/json request body")
    }
  }

  def action() = Action {implicit request: Request[AnyContent] =>
    Ok("action")
  }

  def login() = Action { implicit request: Request[Any] =>
    Ok("login")
  }

  def register() = Action { implicit request: Request[Any] =>
    Ok("register")
  }
}
