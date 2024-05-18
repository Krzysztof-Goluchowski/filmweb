package movieChip

import com.raquo.laminar.api.L.{*, given}
import models.Movie
import org.scalajs.dom.console.log
import org.scalajs.dom.window._

class MovieChip(movie: Movie) {
    def renderMovieChip(): Element = {
        div(
            cls := "movie-chip",
            p(
                cls := "movie-name",
                {movie.movieName}
            ),
            p(
                cls := "movie-rating",
                s"Rating: ${movie.averageRating}"
            ),
            p(
                cls := "movie-rating",
                s"Short description: ${movie.shortDescription match {
                    case Some(value) => value
                    case _ => "No description provided..."
                }}"
            ),
            p(
                cls := "movie-category",
                s"Category: ${movie.category}"
            ),
            button(
                typ("button"),
                "Details",
                onClick --> { _ =>
                   location.href = s"http://localhost:5173/details/${movie.movieId}"
                }
            )
        )
    }
}