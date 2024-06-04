package components

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.console.{log, error}
import scala.scalajs.js
import upickle.default._
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom.window.{alert, localStorage}
import models.responses._
import models.rating._
import icons.Icons._
import com.raquo.laminar.api.A._
import scala.concurrent.ExecutionContext.Implicits.global
import io.laminext.fetch.upickle._
import com.raquo.airstream.core.EventStream._
import components.implicits.{rw, owner}

object RatingForm {
    def sendRating(movieId: Long, stars: Int, review: String): Unit = {
        val userId = localStorage.getItem("userId")

        if (userId != null && !userId.isEmpty){
            val data = write(Rating(movieId.toInt, userId.toInt, stars, review))

            Fetch.post(
                url = "http://localhost:9000/rate",
                body = data,
            ).decodeEither[ErrorResponse, SuccessResponse]
            .foreach(response => 
                response.data match {
                    case Left(error) => {
                        alert(error.message)
                    }
                    case Right(success) => {
                        alert(success.message)
                    }
                }    
            )
        } else {
            alert("You need to log in to rate a movie!")
        }
    }

    def renderRatingForm(movieId: Long): Element = {    
        val starsVar = Var(4)
        val reviewVar = Var("")

        div(
            p(movieId),
            p("Have you watched the movie? Let us know what you think about it!"),
            (1 to 5).map(i => {
                a(
                    onClick --> starsVar.set(i),
                    child <-- starsVar.signal.map(curr => {
                        if (i <= starsVar.signal.now()) filledStar() else emptyStar()
                    })
                )
            }),
            textArea(
                width := "100%",
                rows := 5,
                placeholder("I think it is really great movie!"),
                controlled(
                    value <-- reviewVar.signal,
                    onInput.mapToValue --> reviewVar,
                )
            ),
            button(
                "Send",
                onClick.preventDefault.mapTo(()) --> { _ => sendRating(movieId, starsVar.now(), reviewVar.now()) }
            )
        )
    }
}