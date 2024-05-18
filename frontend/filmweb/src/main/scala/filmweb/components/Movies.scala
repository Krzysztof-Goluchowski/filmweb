package movies

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.log
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import movieChip.MovieChip
import models._

val allCategories = (Category.values.map(category => category.toString) :+ "").sorted

object Movies {

  val categoryVar = Var(Option.empty[String])

  case class FetchOption(name: String, baseUrl: String, bustCache: Boolean = false) {
    def id: String = "fetch-" + name
    def url: String = if (bustCache) baseUrl + "?t=" + js.Date.now() else baseUrl
  }

  private val options = List(
    FetchOption("Valid Fetch request", "http://localhost:9000/movies"),
  )

  def movies(): Element = {
    val selectedOptionVar = Var(options.head)
    val eventsVar = Var(Seq.empty[Movie])

    form(
      p(
        cls := "category-select",
        label("Categories: "),
        select(
         onChange.mapToValue.map(value => if (value == "") None else Some(value)) --> categoryVar,
          value <-- categoryVar.signal.map(_.getOrElse("")),
          allCategories.map(category =>
            option(
              value(category),
              category
            )
          )
        )
      ),
      div(
        button(
          typ("button"),
          "Browse",
          inContext { thisNode =>
            val clicks = thisNode.events(onClick).sample(selectedOptionVar.signal)
            val responses = clicks.flatMap { opt =>
              FetchStream.get(url = opt.url)
                .map(resp => read[Seq[Movie]](resp))
                .map(resp => resp.filter(movie => categoryVar.now().isEmpty || categoryVar.now().contains(movie.category)))
                .recover { case err: Throwable => Some(Seq.empty[Movie]) }
            }

            responses --> { movies =>
              eventsVar.set(movies)
            }
          }
        )
      ),
      div(
        fontSize.em(0.8),
        br(),
        div(children <-- eventsVar.signal.map(_.map(el => new MovieChip(el).renderMovieChip())))
      )
    )
  }
}