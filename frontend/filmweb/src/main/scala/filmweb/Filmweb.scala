package filmweb

import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom
import com.raquo.laminar.api.features.unitArrows
import components.TopBar._
import routing.Routing._
import frontroute.*

@main
def Filmweb(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      topbar(),
      routing()
    )
)
