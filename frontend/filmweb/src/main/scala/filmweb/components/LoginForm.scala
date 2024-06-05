package components

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.console.{log, error}
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.window.{alert, localStorage}
import models.responses._
import models.user._
import icons.Icons._
import com.raquo.laminar.api.A._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import io.laminext.fetch.upickle._
import com.raquo.airstream.core.EventStream._
import components.implicits.{rw, owner}

case class LoginState(login: String = "", password: String = "")

object LoginForm {
    def loginForm(): Element = {
        val loggingState = Var(LoginState())
        val loginWriter = loggingState.updater[String]((state, login) => state.copy(login = login))
        val passwordWriter = loggingState.updater[String]((state, password) => state.copy(password = password))
        val submitter = Observer[LoginState] { state =>
            val data = write(Login(state.login, state.password))

            Fetch.post(
                url = "http://localhost:9000/login",
                body = data
            ).decodeEither[ErrorResponse, LoginResponse]
            .foreach { response =>
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(success) => {
                        alert(s"Successully logged in user ${success.userId.toString()}")
                        localStorage.setItem("userId", success.userId.toString())
                    }
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