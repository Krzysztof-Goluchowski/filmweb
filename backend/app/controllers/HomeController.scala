package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val db = Database.forConfig("postgres")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello world");
  }

  def movies(category: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    val query = sql"select name from users where name = 'Alex'".as[String]
    db.run(query).map { users =>
      Ok(users.mkString(", "))
    }.recover {
      case ex: Exception => InternalServerError("An error occurred while retrieving users: " + ex.getMessage)
    }
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
