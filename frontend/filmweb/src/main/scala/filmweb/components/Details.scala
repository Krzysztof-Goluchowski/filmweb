package details

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.{log, error}
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.window
import models._
import icons.Icons._
import org.scalajs.dom.ext.Ajax
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.A._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global


class Details (movieId: Long) {
    def renderRatingForm(): Element = {
        val stars = Var(4)
        val review = Var("")
        // val userId = window.localStorage.getItem("userId")
        val userId = 1 // to change later

        def sendRating(): Unit = {
            val data = write(Rating(movieId.toInt, userId.toInt, stars.now(), review.now()))

            Ajax.post(
                url = "http://localhost:9000/rate",
                data = data,
                headers = Map("Content-Type" -> "application/json")
            ).onComplete { xhr =>
                if (xhr.isSuccess) {
                    log("Rating submitted successfully")
                } else {
                    error("Failed to submit rating")
                }
            }
        }

        div(
            p(movieId),
            p("Have you watched the movie? Let us know what do you think about it!"),
            (1 to 5).map(i => {
                a(
                    onClick --> stars.set(i),
                    child <-- stars.signal.map(curr => {
                        if (i <= stars.signal.now()) filledStar() else emptyStar()
                    })
                )
            }),
            textArea(
                width := "100%",
                rows := 5,
                placeholder("I think it is really great movie!"),
                controlled(
                    value <-- review,
                    onInput.mapToValue --> review
                )
            ),
            button(
                "Send",
                onClick.preventDefault.mapTo(()) --> { _ => sendRating() }
            )
        )
    }

    def renderStars(rating: Double): Element = {
        val intPart: Integer = rating.toInt
        val decimalPart: Double = rating - rating.toInt

        div(
            (1 to intPart).map(el => filledStar()),
            {if (decimalPart > 0) Some(halfStar()) else p()},
            (1 to (5 - intPart - (if (decimalPart > 0) 1 else 0))).map(el => emptyStar())
        )
    }

    def renderInfo(movie: Movie): Element = {
        div(
            div(
                cls := "details-title-category-rating",

                h1(
                    s"${movie.movieName} / ",
                    span(
                        cls := "category",
                        movie.category
                    )
                ),
                div(
                    renderStars(movie.averageRating),   
                    cls := "details-rating",
                    h1(
                        cls := "movie-title",
                        {movie.averageRating},
                    ),
                )
            ),
            h4(cls := "short-description", movie.shortDescription),
            p(cls := "long-description", movie.longDescription),
            renderRatingForm()
        )
    }

    def renderDetails(): Element = { 
        val movieDetailsVar = Var(Option.empty[Movie])

        form(
            cls := "movie-details",
            inContext { thisNode => 
                val responses = FetchStream.get(url = s"http://localhost:9000/details/${movieId}")
                    .map(resp => read[Movie](resp))
                    .recover {case err: java.lang.Throwable => Option.empty[Movie]}
                
                responses --> { maybeMovie =>
                    movieDetailsVar.set(Some(maybeMovie))
                }
            },
            cls := "card",
            img(cls := "card-image", src := "../../../../image.jpg", width := "100%"),
            div(
                fontSize.em(0.8),
                br(),
                child <-- movieDetailsVar.signal.map {
                    case Some(el) => renderInfo(el)
                    case None => p("Loading...")
                }
            ),
        )
    }
}