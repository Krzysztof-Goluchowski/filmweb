package components

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.{log}
import org.scalajs.dom.window.{alert}
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import components.MovieChip._
import models.responses._
import models.movies._
import io.laminext.fetch.upickle._
import scala.concurrent.ExecutionContext.Implicits.global
import components.implicits.{rw, owner}

object Movies {
    val fetchedCategoriesVar = Var(Seq.empty[String])
    val fetchedMoviesVar = Var(Seq.empty[MovieDetails])

    def fetchCategories(): Unit = {
        Fetch.get(
            url = "http://localhost:9000/categories"
        ).decodeEither[ErrorResponse, Seq[String]]
        .foreach { 
            response => {
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(categories) => {
                        log(categories.toString())
                        fetchedCategoriesVar.set((categories :+ "---").sorted)
                    }
                }
            }
        }
    }

    def fetchMovies(url: String, category: Option[String] = None): Unit = {
        Fetch.get(
            url = url
        ).decodeEither[ErrorResponse, Seq[MovieDetails]]
        .foreach { 
            response => {
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(movies) => {
                        val filteredMovies = category match {
                            case Some(categoryToFilter) => {
                                movies.filter(movie => categoryToFilter == movie.category)
                            }
                            case None => {
                                movies
                            }
                        }
                        log(filteredMovies.toString())
                        fetchedMoviesVar.set(filteredMovies)
                    }
                }
            }
        }
    }

    def renderMovies(url: String): Element = {
        val categoryVar = Var(Option.empty[String])
        fetchCategories();

        form(
            p(
                cls := "category-select",
                label("Categories: "),
                select(
                    onChange.mapToValue.map(value => if (value == "---") None else Some(value)) --> categoryVar,
                    value <-- categoryVar.signal.map(_.getOrElse("---")),
                    children <-- fetchedCategoriesVar.signal.map { 
                        categories =>
                        categories.map { 
                            category =>
                                option(
                                    value(category),
                                    category
                                )
                        }
                    }
                )
            ),
            div(
                button(
                    typ("button"),
                    "Browse",
                    onClick --> {categoryVar.now() match {
                            case Some(category) => fetchMovies(url, Some(category))
                            case None => fetchMovies(url)
                        }
                    }
                )
            ),
            div(
                fontSize.em(0.8),
                br(),
                div(children <-- fetchedMoviesVar.signal.map(_.map(el => MovieChip.renderMovieChip(el))))
            ),
        )
    }
}