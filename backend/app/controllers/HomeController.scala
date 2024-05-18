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
import upickle.default._

case class Movie(movieName: String, averageRating: Double, category: String)
object Movie {
  implicit val rw: ReadWriter[Movie] = macroRW
}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val db = Database.forConfig("postgres")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello World!");
  }

  def movies(category: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    category match {
      case Some(cat) =>
        val query = sql"select movie_name, average_rating, category from movies where category = $cat".as[(String, Double, String)]
        db.run(query).map { movies =>
          val movieList = movies.map { case (name, rating, category) => Movie(name, rating, category) }
          Ok(write(movieList))
        }
      case None =>
        val query = sql"select movie_name, average_rating, category from movies".as[(String, Double, String)]
        db.run(query).map { movies =>
          val movieList = movies.map { case (name, rating, category) => Movie(name, rating, category) }
          Ok(write(movieList))
        }
    }
  }

  def recommended(userId: Int) = Action {implicit request: Request[Any] =>
    Ok("recommended");
  }

  def details(movieId: Int) = Action {implicit request: Request[Any] =>
    Ok("details of movie with id " + movieId);
  }

  def rate() = Action.async {implicit request: Request[AnyContent] =>
    val requestBodyJson = request.body.asJson
    requestBodyJson.map { json =>
      val movieId = (json \ "movieId").as[Int]
      val userId = (json \ "userId").as[Int]
      val stars = (json \ "stars").as[Int]
      val review = (json \ "review").as[String]

      hasAlreadyGivenRating(userId, movieId).flatMap { hasRated =>
        if (!hasRated) {
          postNewRating(movieId, userId, stars, review)
        } else {
          Future.successful(BadRequest(Json.obj("message" -> "This user already rated this film.")))
        }
      }
    }.getOrElse {
      Future.successful(BadRequest("Expecting application/json request body"))
    }
  }

  def hasAlreadyGivenRating(userId: Int, movieId: Int): Future[Boolean] = {
    val query = sql"SELECT stars FROM ratings WHERE user_id = $userId AND movie_id = $movieId".as[Double]
    db.run(query.headOption).map {
      case Some(rating) =>
        true
      case _ =>
        false
    }
  }

  def postNewRating(movieId: Int, userId: Int, stars: Double, review: String) = {
    val query = sql"SELECT average_rating, num_ratings FROM movies WHERE movie_id = $movieId".as[(Double, Int)]

    db.run(query.headOption).flatMap {
      case Some(average_rating, num_ratings) =>
        val newNumRatings = num_ratings + 1
        val newRating = ((num_ratings * average_rating) + stars) / newNumRatings

        val updateQuery = sqlu"UPDATE movies SET average_rating = $newRating , num_ratings = $newNumRatings WHERE movie_id = $movieId"
        val insertReviewQuery = sqlu"INSERT INTO ratings (movie_id, user_id, stars, review) VALUES ($movieId, $userId, $stars, $review)"

        for {
          _ <- db.run(updateQuery)
          _ <- db.run(insertReviewQuery)
        } yield Ok(Json.obj("message" -> "Added new rating and review"))

      case None =>
        Future.successful(NotFound(Json.obj("message" -> "There isn't such film!")))
    }
  }

  def action() = Action {implicit request: Request[AnyContent] =>
    Ok("action")
  }

  def login() = Action.async {implicit request: Request[AnyContent] =>
    val requestBodyJson = request.body.asJson
    requestBodyJson.map { json =>
      val givenLogin = (json \ "login").as[String]
      val givenPassword = (json \ "password").as[String]

      val query = sql"select password from users where login = $givenLogin".as[String]

      db.run(query.headOption).map {
        case Some(password) if password == givenPassword =>
          Ok(Json.obj("message" -> "Zalogowano poprawnie"))
        case _ =>
          Unauthorized(Json.obj("message" -> "Nieprawidłowy login lub hasło"))
      }

    }.getOrElse {
      Future.successful(BadRequest("Expecting application/json request body"))
    }
  }

  def register() = Action.async(parse.json) { implicit request =>
    val json = request.body

    val maybeGivenFirstName = (json \ "firstname").asOpt[String]
    val maybeGivenLastnName = (json \ "lastname").asOpt[String]
    val maybeGivenLogin = (json \ "login").asOpt[String]
    val maybeGivenPassword = (json \ "password").asOpt[String]

    (maybeGivenFirstName, maybeGivenLastnName, maybeGivenLogin, maybeGivenPassword) match {
      case (Some(givenFirstName), Some(givenLastName), Some(givenLogin), Some(givenPassword)) =>
        validateCredentials(givenLogin, givenPassword).flatMap { isValid =>
          if (!isValid) {
            Future.successful(BadRequest(Json.obj("message" -> "Nieprawidłowe dane rejestracji (użytkownik już w bazie lub hasło i login nie mają 8 znaków)")))
          } else {
            val insertQuery = sql"INSERT INTO users (firstname, lastname, login, password) VALUES ($givenFirstName, $givenLastName, $givenLogin, $givenPassword)".as[Int]
            db.run(insertQuery).map { _ =>
              Ok(Json.obj("message" -> "Użytkownik został pomyslnie zarejestronwany, proszę się teraz zalogować"))
            }
          }

        }
      case _ =>
        Future.successful(BadRequest(Json.obj("message" -> "Nieprawidłowe dane do rejestracji")))
    }
  }

  def validateCredentials(login: String, password: String): Future[Boolean] = {
    val validLength = login.length >= 8 && password.length >= 8
    if (!validLength) {
      Future.successful(false)
    } else {
      val query = sql"SELECT firstname FROM users WHERE login = $login".as[String]
      db.run(query.headOption).map {
        case Some(_) =>
          false
        case _ =>
          true
      }
    }
  }

}
