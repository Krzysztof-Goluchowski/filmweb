package controllers

import javax.inject._
import play.api.mvc._
import services.UserService


@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {

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