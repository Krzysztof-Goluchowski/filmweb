package movieChip

import com.raquo.laminar.api.L.{*, given}
import models.Movie
import org.scalajs.dom.console.log
import org.scalajs.dom.window._
import icons.Icons._

class MovieChip(movie: Movie) {
    def renderStars(rating: Double): Element = {
        val intPart: Integer = rating.toInt
        val decimalPart: Double = rating - rating.toInt

        div(
            (1 to intPart).map(el => filledStar()),
            {if (decimalPart > 0) Some(halfStar()) else p()},
            (1 to (5 - intPart - (if (decimalPart > 0) 1 else 0))).map(el => emptyStar())
        )
    }

    def renderMovieChip(): Element = {
        div(
            cls := "movie-chip",
            div(
                cls := "movie-name-rating",
                div(
                    cls := "movie-name-category",
                    p(
                        cls := "movie-name",
                        movie.movieName
                    ),
                    p(
                        cls := "movie-category",
                        s"/ ${movie.category}"
                    )
                ),
                div(
                    cls := "movie-rating",
                    p(
                        cls := "",
                        movie.averageRating.toString
                    ),
                    renderStars(movie.averageRating)
                )
            ),
            p(
                cls := "movie-description",
                s"Short description: ${movie.shortDescription.getOrElse("No description provided...")}"
            ),
            button(
                typ := "button",
                "Details",
                onClick --> { _ =>
                    location.href = s"http://localhost:5173/details/${movie.movieId}"
                }
            )
        )
    }
}