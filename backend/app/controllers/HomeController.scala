package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import models.Movie
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.mvc.Results._
import upickle.default._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val db = Database.forConfig("postgres")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello World!");
  }

  def movies(category: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    category match {
      case Some(cat) =>
        val query = sql"""
              SELECT movie_id, movie_name, average_rating, category
              FROM movies
              WHERE category = $cat
              """.as[(Int, String, Double, String)]

        db.run(query).map { movies =>
          val movieList = movies.map { case (id, name, rating, category) => Movie(id, name, rating, category) }
          Ok(write(movieList))
        }
      case None =>
        val query = sql"""
               SELECT movie_id, movie_name, average_rating, category
               FROM movies
               """.as[(Int, String, Double, String)]

        db.run(query).map { movies =>
          val movieList = movies.map { case (id, name, rating, category) => Movie(id, name, rating, category) }
          Ok(write(movieList))
        }
    }
  }

  def recommended(userId: Int) = Action.async { implicit request: Request[AnyContent] =>
    val query = sql"""
           SELECT movie_id, movie_name, average_rating, category, short_description, long_description
           FROM movies
           LIMIT 10
           """.as[(Int, String, Double, String, Option[String], Option[String])]

    db.run(query).map { movies =>
      val movieList = movies.map { case (id, name, rating, category, shortDesc, longDesc) => Movie(id, name, rating, category, shortDesc, longDesc) }
      Ok(write(movieList))
    }
  }

  def details(movieId: Int) = Action.async { implicit request: Request[AnyContent] =>
    val query =  sql"""
           SELECT movie_id, movie_name, average_rating, category, short_description, long_description
           FROM movies
           WHERE movie_id = $movieId
           """.as[(Int, String, Double, String, Option[String], Option[String])]

    db.run(query).map { movie =>
      movie.headOption match {
        case Some(((id, name, rating, category, shortDesc, longDesc))) =>
          val wantedMovie = Movie(id, name, rating, category, shortDesc, longDesc)
          Ok(write(wantedMovie))
        case None =>
          BadRequest("Movie with given id doesn't exist")
      }
    }
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
    val query = sql"""
           SELECT stars
           FROM ratings
           WHERE user_id = $userId AND movie_id = $movieId
           """.as[Double]
    db.run(query.headOption).map {
      case Some(rating) =>
        true
      case _ =>
        false
    }
  }

  def postNewRating(movieId: Int, userId: Int, stars: Double, review: String) = {
    val query = sql"""
           SELECT average_rating, num_ratings
           FROM movies
           WHERE movie_id = $movieId
           """.as[(Double, Int)]

    db.run(query.headOption).flatMap {
      case Some(average_rating, num_ratings) =>
        val newNumRatings = num_ratings + 1
        val newRating = ((num_ratings * average_rating) + stars) / newNumRatings

        val updateQuery = sqlu"""
                UPDATE movies
                SET average_rating = $newRating , num_ratings = $newNumRatings
                WHERE movie_id = $movieId
                """
        val insertReviewQuery = sqlu"""
                INSERT INTO ratings (movie_id, user_id, stars, review)
                VALUES ($movieId, $userId, $stars, $review)
                """

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

      val query = sql"""
             SELECT password
             FROM users
             WHERE login = $givenLogin
             """.as[String]

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

  def register() = Action.async {implicit request: Request[AnyContent] =>
      request.body.asJson.map { json =>
      val givenFirstName = (json \ "firstName").as[String]
      val givenLastName = (json \ "lastName").as[String]
      val givenLogin = (json \ "login").as[String]
      val givenPassword = (json \ "password").as[String]

      validateCredentials(givenLogin, givenPassword).flatMap { isValid =>
        if (!isValid){
          Future.successful(BadRequest(Json.obj("message" -> "Registration failed (User is already in the database or login and password do not have a length of 8 characters)")))
        } else {
          val insertQuery = sql"""
                 INSERT INTO users (firstname, lastname, login, password)
                 VALUES ($givenFirstName, $givenLastName, $givenLogin, $givenPassword)
                 """.as[Int]

          db.run(insertQuery).map { _ =>
            Ok(Json.obj("message" -> "Użytkownik został pomyslnie zarejestronwany, proszę się teraz zalogować"))
          }
        }
      }
    }.getOrElse {
      Future.successful(BadRequest(Json.obj("message" -> "Nieprawidłowe dane do rejestracji")))
    }
  }

  def validateCredentials(login: String, password: String): Future[Boolean] = {
    val validLength = login.length >= 8 && password.length >= 8
    if (!validLength) {
      Future.successful(false)
    } else {
      val query = sql"""
             SELECT firstname
             FROM users
             WHERE login = $login
             """.as[String]

      db.run(query.headOption).map {
        case Some(_) =>
          false
        case _ =>
          true
      }
    }
  }

}