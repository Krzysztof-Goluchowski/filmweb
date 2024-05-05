package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.mvc.Results._

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
    val query = sql"select login from users".as[String]
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

  def login() = Action.async(parse.json) { implicit request =>
    val json = request.body
    val maybeGivenLogin = (json \ "login").asOpt[String]
    val maybeGivenPassword = (json \ "password").asOpt[String]

    (maybeGivenLogin, maybeGivenPassword) match {
      case (Some(givenLogin), Some(givenPassword)) =>
        val query = sql"select password from users where login = $givenLogin".as[String]
        db.run(query.headOption).map {
          case Some(password) if password == givenPassword =>
            Ok(Json.obj("message" -> "Zalogowano poprawnie"))
          case _ =>
            Unauthorized(Json.obj("message" -> "Nieprawidłowy login lub hasło"))
        }.recover {
          case ex: Exception => InternalServerError("An error occurred while logging in: " + ex.getMessage)
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("message" -> "Nieprawidłowe dane logowania")))
    }
  }

  def register() = Action { implicit request: Request[Any] =>
    Ok("register")
  }
}
