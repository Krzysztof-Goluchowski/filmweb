package services

import javax.inject._
import repositories.UserRepository
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.Json
import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.mvc.Results._
import services.UserService

@Singleton
class UserService @Inject()(userRepository: UserRepository)(implicit ec: ExecutionContext) {

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

}

