package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.User
import models.Movie
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.mvc.Results._
import upickle.default._

@Singleton
class MovieController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val db = Database.forConfig("postgres")

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("Hello World!");
  }

  def movies(category: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    category match {
      case Some(cat) =>
        val query = sql"""
              SELECT movie_id, movie_name, average_rating, category, num_ratings
              FROM movies
              WHERE category = $cat
              """.as[(Int, String, Double, String, Int)]

        db.run(query).map { movies =>
          val movieList = movies.map { case (id, name, rating, category, numRatings) =>
            Movie(id, name, rating, category, numRatings) }
          Ok(write(movieList))
        }
      case None =>
        val query = sql"""
               SELECT movie_id, movie_name, average_rating, category, num_ratings
               FROM movies
               """.as[(Int, String, Double, String, Int)]

        db.run(query).map { movies =>
          val movieList = movies.map { case (id, name, rating, category, numRatings) =>
            Movie(id, name, rating, category, numRatings) }
          Ok(write(movieList))
        }
    }
  }

  def recommended(userId: Int) = Action.async { implicit request: Request[AnyContent] =>
    val query = sql"""
           SELECT movie_id, movie_name, average_rating, category, num_ratings, short_description, long_description
           FROM movies
           LIMIT 10
           """.as[(Int, String, Double, String, Int, Option[String], Option[String])]

    db.run(query).map { movies =>
      val movieList = movies.map { case (id, name, rating, category, numRatings, shortDesc, longDesc) =>
        Movie(id, name, rating, category, numRatings, shortDesc, longDesc) }
      Ok(write(movieList))
    }
  }

  def details(movieId: Int) = Action.async { implicit request: Request[AnyContent] =>
    val query =  sql"""
           SELECT movie_id, movie_name, average_rating, category, num_ratings, short_description, long_description
           FROM movies
           WHERE movie_id = $movieId
           """.as[(Int, String, Double, String, Int, Option[String], Option[String])]

    db.run(query).map { movie =>
      movie.headOption match {
        case Some(((id, name, rating, category, numRatings, shortDesc, longDesc))) =>
          val wantedMovie = Movie(id, name, rating, category, numRatings, shortDesc, longDesc)
          Ok(write(wantedMovie))
        case None =>
          BadRequest("Movie with given id doesn't exist")
      }
    }
  }
}