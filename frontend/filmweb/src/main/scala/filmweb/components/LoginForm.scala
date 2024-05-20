package loginForm

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.console.{log, error}
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.window
import models._
import icons.Icons._
import com.raquo.laminar.api.A._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global

case class LoginState(login: String = "", password: String = "")

object LoginForm {
    def loginForm(): Element = {
        val loggingState = Var(LoginState())
        val loginWriter = loggingState.updater[String]((state, login) => state.copy(login = login))
        val passwordWriter = loggingState.updater[String]((state, password) => state.copy(password = password))
        val submitter = Observer[LoginState] { state =>
            val data = write(Login(state.login, state.password))

            Ajax.post(
                url = "http://localhost:9000/login",
                data = data,
                headers = Map("Content-Type" -> "application/json")
            ).onComplete { xhr =>
                if (xhr.isSuccess) {
                    val userId = xhr.get.responseText
                    window.localStorage.setItem("userId", userId)
                    log(userId)
                } else {
                    error("Failed to create account")
                }
            }
        }

        div(
            width := "40vw",
            cls := "card",
            h1(
                "Log in to discover all features of our app..."
            ),
            form(
                onSubmit
                .preventDefault
                .mapTo(loggingState.now()) --> submitter,
                h4(
                    textAlign := "left",
                    label("Enter your login: "),
                ),
                input(
                    width := "100%",
                    placeholder("login123"),
                    controlled(
                        value <-- loggingState.signal.map(_.login),
                        onInput.mapToValue --> loginWriter
                    )
                ),
                h4(
                    textAlign := "left",
                    label("Enter your password: "),
                ),
                input(
                    width := "100%",
                    typ := "password",
                    placeholder("password123"),
                    controlled(
                        value <-- loggingState.signal.map(_.password),
                        onInput.mapToValue --> passwordWriter
                    )
                ),
                p(),
                button(typ("submit"), "Submit"),
                p(
                    "Do not have an account yet? Create new one ",
                    a(
                        "here", 
                        href := "/register"
                    )
                ),
            )
        )
    }
}