package routing

import com.raquo.laminar.api.L._
import frontroute._
import components.RegisterForm._
import components.Movies._
import components.Details._
import components.LoginForm._
import org.scalajs.dom.window.{alert, localStorage}
import org.scalajs.dom.console.{log}


object Routing {
    def routing() = {
        routes(
            div(
                cls := "p-4 min-h-[300px]",
                pathEnd {
                    div("Index page.")
                },
                path("login") {
                    div(
                        loginForm()
                    )
                },
                path("register") {
                    div(
                        registerForm()
                    )
                },
                path("details" / long) { id =>
                    div(
                       renderDetails(id)
                    )
                },
                path("recommended") {
                    val userId = localStorage.getItem("userId")

                    if (userId != null && !userId.isEmpty){
                        div(
                            h2(
                                s"Recommended movies for user ${userId}"
                            ),
                            renderMovies(s"http://localhost:9000/recommended/${userId}")
                        )                    
                    } else {
                        div(
                            h2(
                                "All movies (log in to see recommended movies)"
                            ),
                            renderMovies("http://localhost:9000/movies")
                        )
                    }
                },
                path("movies") {
                    div(
                        h2(
                            "All movies"
                        ),
                        renderMovies("http://localhost:9000/movies")
                    )
                },
                (noneMatched & extractUnmatchedPath) { unmatched =>
                    div(
                        div("Not Found"),
                        div(unmatched.mkString("/", "/", ""))
                    )
                }
            )
        )
    }
}
