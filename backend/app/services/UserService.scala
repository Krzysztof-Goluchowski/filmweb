package services

import repositories.UserRepository
import repositories.RatingRepository
import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.mvc.Results._

@Singleton
class UserService @Inject()(userRepository: UserRepository, ratingRepository: RatingRepository)(implicit ec: ExecutionContext) {

  def login(request: Request[AnyContent]): Future[Result] = {
    request.body.asJson.map { json =>
      val givenLogin = (json \ "login").as[String]
      val givenPassword = (json \ "password").as[String]

      userRepository.findPasswordByLogin(givenLogin).map {
        case Some(password) if password == givenPassword =>
          Ok(Json.obj("message" -> "Zalogowano poprawnie"))
        case _ =>
          Unauthorized(Json.obj("message" -> "Nieprawidłowy login lub hasło"))
      }
    }.getOrElse {
      Future.successful(BadRequest("Expecting application/json request body"))
    }
  }

  def register(request: Request[AnyContent]): Future[Result] = {
    request.body.asJson.map { json =>
      val givenFirstName = (json \ "firstName").as[String]
      val givenLastName = (json \ "lastName").as[String]
      val givenLogin = (json \ "login").as[String]
      val givenPassword = (json \ "password").as[String]

      userRepository.validateCredentials(givenLogin, givenPassword).flatMap { isValid =>
        if (!isValid) {
          Future.successful(BadRequest(Json.obj("message" -> "Registration failed (User is already in the database or login and password do not have a length of 8 characters)")))
        } else {
          userRepository.createUser(givenFirstName, givenLastName, givenLogin, givenPassword).map { _ =>
            Ok(Json.obj("message" -> "Użytkownik został pomyślnie zarejestrowany, proszę się teraz zalogować"))
          }
        }
      }.recover {
        case _ =>
          BadRequest("Nieprawidłowe dane do rejestracji")
      }
    }.getOrElse {
      Future.successful(BadRequest("Oczekiwano ciała żądania w formacie JSON"))
    }
  }

  def rate(request: Request[AnyContent]): Future[Result] = {
    request.body.asJson.map { json =>
      val movieId = (json \ "movieId").as[Int]
      val userId = (json \ "userId").as[Int]
      val stars = (json \ "stars").as[Int]
      val review = (json \ "review").as[String]

      ratingRepository.hasAlreadyGivenRating(userId, movieId).flatMap { hasRated =>
        if (!hasRated) {
          ratingRepository.createRating(movieId, userId, stars, review).map { _ =>
            Ok(Json.obj("message" -> "Rating created successfully"))
          }
        } else {
          Future.successful(BadRequest(Json.obj("message" -> "This user already rated this film.")))
        }
      }
    }.getOrElse {
      Future.successful(BadRequest("Expecting application/json request body"))
    }
  }

}

