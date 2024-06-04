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

  def login(request: Request[JsValue]): Future[Result] = {
    parseAndValidate[Login](request) match {
      case Right(login) =>
        userRepository.findPasswordByLogin(login.login).map {
          case Some(user) if user.password == login.password =>
            Ok(Json.obj("userId" -> user.userId))
          case _ =>
            Unauthorized(Json.obj("message" -> "Wrong login or password"))
        }
      case Left(errorResult) => Future.successful(errorResult)
    }
  }

  def register(request: Request[JsValue]): Future[Result] = {
    parseAndValidate[UserRegistration](request) match {
      case Right(user) =>
        userRepository.validateCredentials(user.login, user.password).flatMap {
          case true =>
            userRepository.createUser(user.firstName, user.lastName, user.login, user.password).map { _ =>
              Ok(Json.obj("message" -> "Registered successfully"))
            }
          case false =>
            Future.successful(BadRequest(Json.obj("message" -> "Registration failed (User is already in the database or login and password do not have a length of 8 characters)")))
        }.recoverWith {
          case _ => Future.successful(BadRequest(Json.obj("message" -> "Registration failed due to server error")))
        }
      case Left(errorResult) => Future.successful(errorResult)
    }
  }

  def rate(request: Request[JsValue]): Future[Result] = {
    parseAndValidate[Rating](request) match {
      case Right(rating) =>
        ratingRepository.hasAlreadyGivenRating(rating.userId, rating.movieId).flatMap {
          case false =>
            ratingRepository.createRating(rating.movieId, rating.userId, rating.stars, rating.review).map { _ =>
              Ok(Json.obj("message" -> "Rating created successfully"))
            }
          case true =>
            Future.successful(BadRequest(Json.obj("message" -> "This user already rated this film.")))
        }
      case Left(errorResult) => Future.successful(errorResult)
    }
  }

  private def parseAndValidate[T](request: Request[JsValue])(implicit reads: Reads[T]): Either[Result, T] = {
    val jsonString = request.body.as[String]
    val json = Json.parse(jsonString)
    json.validate[T] match {
      case JsSuccess(value, _) => Right(value)
      case JsError(errors) =>
        Left(BadRequest(Json.obj("message" -> "Invalid JSON", "details" -> JsError.toJson(errors))))
    }
  }
}
