package components

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.{log}
import org.scalajs.dom.window.{alert}
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import models.movies._
import models.responses._
import icons.Icons._
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.A._
import scala.concurrent.ExecutionContext.Implicits.global
import components.RatingForm._
import io.laminext.fetch.upickle._
import components.implicits.{rw, owner}

object Details {
    val movieDetailsVar = Var(Option.empty[MovieDetails])

    def fetchMovieDetails(movieId: Long): Unit = {
        Fetch.get(
            url = s"http://localhost:9000/details/${movieId}"
        ).decodeEither[ErrorResponse, MovieDetails]
        .foreach { 
            response => {
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(details) => {
                        log(details.toString())
                        movieDetailsVar.set(Some(details))
                    }
                }
            }
        }
    }

    def renderStars(rating: Double): Element = {
        val intPart: Integer = rating.toInt
        val decimalPart: Double = rating - rating.toInt

        div(
            (1 to intPart).map(el => filledStar()),
            {if (decimalPart > 0) halfStar() else p()},
            (1 to (5 - intPart - (if (decimalPart > 0) 1 else 0))).map(el => emptyStar())
        )
    }

    def renderInfo(movie: MovieDetails): Element = {
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
            renderRatingForm(movie.movieId)
        )
    }

    def renderDetails(movieId: Long): Element = { 
        fetchMovieDetails(movieId)

        form(
            cls := "movie-details card",
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