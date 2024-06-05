package controllers

import javax.inject._
import play.api.mvc._
import services.UserService
import play.api.libs.json._


@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, userService: UserService) extends BaseController {

  def login(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    userService.login(request)
  }

  def register(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    userService.register(request)
  }

  def rate(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    userService.rate(request)
  }
}