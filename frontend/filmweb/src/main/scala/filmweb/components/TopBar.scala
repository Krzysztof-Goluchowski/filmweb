package topbar

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom
import com.raquo.laminar.api.features.unitArrows

object TopBar {
    def topbar(): Element = {
        div(
            cls := "navbar",
            ul(
                cls := "navbar-list",
                li(
                    cls := "navbar-item",
                    a(href := "/recommended", cls := "navbar-link", "Recommended")
                ),
                li(
                    cls := "navbar-item",
                    a(href := "/movies", cls := "navbar-link", "Movies")
                )
            ),
            div(
                cls := "login-button navbar-item",
                a(
                    "Sign in",
                    href := "/login"
                )
            )
        )
    }
}