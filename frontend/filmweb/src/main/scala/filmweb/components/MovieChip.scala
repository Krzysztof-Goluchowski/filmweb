package movieChip

import com.raquo.laminar.api.L.{*, given}
import models.Movie

class MovieChip(movie: Movie) {
    def renderMovieChip(): Element = {

        div(
            p({movie.movieName}),
            p({movie.movieId})
        )
    }
}

