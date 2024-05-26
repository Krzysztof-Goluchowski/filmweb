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
import services.UserService


@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {
  val db = Database.forConfig("postgres")

  def login() = Action.async { implicit request: Request[AnyContent] =>
    userService.login(request)
  }

  def register() = Action.async { implicit request: Request[AnyContent] =>
    userService.register(request)
  }

  def rate() = Action.async { implicit request: Request[AnyContent] =>
    userService.rate(request)
  }

}