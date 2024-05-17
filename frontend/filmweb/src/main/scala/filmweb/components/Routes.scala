package routingPackage

import com.raquo.laminar.api.L._
import frontroute._
import loginForm.LoginForm._
import registerForm.RegisterForm._
import movies.Movies._
import org.scalajs.dom._


object Routing {
    def routing() = 
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
                path("recommended") {
                    val userId = window.localStorage.getItem("login")
                    
                    div(
                        p(s"Recommended movies for: $userId")
                    )
                },
                path("movies") {
                    div(
                        movies()
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
