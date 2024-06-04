package components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.console.{log, error}
import upickle.default._
import org.scalajs.dom.window.{alert, localStorage}
import models.responses._
import models.user._
import icons.Icons._
import com.raquo.laminar.api.A._
import scala.concurrent.ExecutionContext.Implicits.global
import io.laminext.fetch.upickle._
import com.raquo.airstream.core.EventStream._
import components.implicits.{rw, owner}

case class RegisterState(firstName: String = "", lastName: String = "", login: String = "", password: String = "")

implicit val rw: Reader[ErrorResponse] = macroR[ErrorResponse]

object RegisterForm {
    def registerForm(): Element = {
        val registerState = Var(RegisterState())
        val firstNameWriter = registerState.updater[String]((state, firstName) => state.copy(firstName = firstName))
        val lastNameWriter = registerState.updater[String]((state, lastName) => state.copy(lastName = lastName))
        val loginWriter = registerState.updater[String]((state, login) => state.copy(login = login))
        val passwordWriter = registerState.updater[String]((state, password) => state.copy(password = password))
        val submitter = Observer[RegisterState] { state =>
            val data = write(Register(state.firstName, state.lastName, state.login, state.password))

            Fetch.post(
                url = "http://localhost:9000/register",
                body = data
            ).decodeEither[ErrorResponse, LoginResponse]
            .foreach { response =>
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(success) => {
                        alert(s"Successully registered user ${success.userId.toString()}")
                    }
                }
            }
        }

        div(
            width := "40vw",
            cls := "card",
            h1(
                "Register to discover all features of our app..."
            ),
            form(
                onSubmit
                .preventDefault
                .mapTo(registerState.now()) --> submitter,
                h4(
                    textAlign := "left",
                    label("First name:"),
                ),
                input(
                    width := "100%",
                    placeholder("Adam"),
                    controlled(
                        value <-- registerState.signal.map(_.firstName),
                        onInput.mapToValue --> firstNameWriter
                    )
                ),
                h4(
                    textAlign := "left",
                    label("Last name:"),
                ),
                input(
                    width := "100%",
                    placeholder("Smith"),
                    controlled(
                        value <-- registerState.signal.map(_.lastName),
                        onInput.mapToValue --> lastNameWriter
                    )
                ),
                h4(
                    textAlign := "left",
                    label("Enter your login:"),
                ),
                input(
                    width := "100%",
                    placeholder("adamsmith123"),
                    controlled(
                        value <-- registerState.signal.map(_.login),
                        onInput.mapToValue --> loginWriter
                    )
                ),
                h4(
                    textAlign := "left",
                    label("Enter your password:"),
                ),
                div(
                    display := "flex",
                    alignItems := "center",
                    input(
                        width := "100%",
                        typ := "password",
                        placeholder("password123"),
                        controlled(
                            value <-- registerState.signal.map(_.password),
                            onInput.mapToValue --> passwordWriter
                        )
                    )
                ),
                p(),
                button(typ("submit"), "Submit"),
                p(
                    "Do you have an account? Sign in ",
                    a(
                        "here", 
                        href := "/login"
                    )
                ),
            )
        )
    }
}