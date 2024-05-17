package movies

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.log
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import movieChip.MovieChip
import models.Movie

object Movies {

    case class FetchOption(name: String, baseUrl: String, bustCache: Boolean = false) {
        def id: String = "fetch-" + name
        def url: String = if (bustCache) baseUrl + "?t=" + js.Date.now() else baseUrl
    }

    private val options = List(
        FetchOption("Valid Fetch request", "http://localhost:9000/movies"),
    )

    def movies(): HtmlElement = {
        val selectedOptionVar = Var(options.head)
        val eventsVar = Var(Seq.empty[Movie])

        form(
            h2("Fetch API tester"),
            div(
                button(
                    typ("button"),
                    "Send",
                    inContext { thisNode =>
                        val clicks = thisNode.events(onClick).sample(selectedOptionVar.signal)
                        val responses = clicks.flatMap { opt =>
                            FetchStream.get(url = opt.url)
                                .map(resp => read[Seq[Movie]](resp))
                                .recover { case err: Throwable => Some(Seq.empty[Movie]) }
                        }

                        responses --> { movies =>
                            eventsVar.set(movies)
                        }
                    }
                ),
                button(
                    typ("button"),
                    "Clear",
                    onClick --> {_ => {
                        eventsVar.update(_ => Seq.empty[Movie])
                    }}
                ),
            ),

            div(
                fontSize.em(0.8),
                br(),
                b("Events:"),
                div(children <-- eventsVar.signal.map(_.map(el => new MovieChip(el).renderMovieChip())))
            )
        )
    }
}