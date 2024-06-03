package services

import repositories.{UserRepository, RatingRepository}
import scala.concurrent.{ExecutionContext, Future}
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results._
import models.{Login, Rating, UserRegistration, User}

@Singleton
class UserService @Inject()(userRepository: UserRepository, ratingRepository: RatingRepository)(implicit ec: ExecutionContext) {

  def login(request: Request[AnyContent]): Future[Result] = {
    parseJson[Login](request) { login =>
      userRepository.findPasswordByLogin(login.login).map {
        case Some(user) if user.password == login.password =>
          Ok(Json.obj("userId" -> user.userId))
        case _ =>
          Unauthorized(Json.obj("message" -> "Wrong login or password"))
      }
    }
  }

  def register(request: Request[AnyContent]): Future[Result] = {
    parseJson[UserRegistration](request) { user =>
      userRepository.validateCredentials(user.login, user.password).flatMap {
        case true =>
          userRepository.createUser(user.firstName, user.lastName, user.login, user.password).map { _ =>
            Ok(Json.obj("message" -> "Registered successfully"))
          }
        case false =>
          Future.successful(BadRequest(Json.obj("message" -> "Registration failed (User is already in the database or login and password do not have a length of 8 characters)")))
      }.recover {
        case _ =>
          BadRequest("Registration failed")
      }
    }
  }

  def rate(request: Request[AnyContent]): Future[Result] = {
    parseJson[Rating](request) { rating =>
      ratingRepository.hasAlreadyGivenRating(rating.userId, rating.movieId).flatMap {
        case false =>
          ratingRepository.createRating(rating.movieId, rating.userId, rating.stars, rating.review).map { _ =>
            Ok(Json.obj("message" -> "Rating created successfully"))
          }
        case true =>
          Future.successful(BadRequest(Json.obj("message" -> "This user already rated this film.")))

      }
    }
  }

  private def parseJson[T](request: Request[AnyContent])(block: T => Future[Result])(implicit reads: Reads[T]): Future[Result] = {
    request.body.asJson.map { json =>
      json.validate[T] match {
        case JsSuccess(value, _) => block(value)
        case JsError(errors) => Future.successful(BadRequest(Json.obj("message" -> "Invalid JSON", "details" -> errors.toString)))
      }
    }.getOrElse {
      Future.successful(BadRequest("Expecting application/json request body"))
    }
  }

}

