//package repositories
//
//import javax.inject.{Inject, Singleton}
//import models.Movie
//import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
//import slick.jdbc.JdbcProfile
//import scala.concurrent.{ExecutionContext, Future}
//
//import scala.concurrent.{ExecutionContext, Future}
//
//@Singleton
//class MovieRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//  private val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//  private class MovieTable(tag: Tag) extends Table[Movie](tag, "movies") {
//    def id = column[Int]("movie_id", O.PrimaryKey, O.AutoInc)
//    def movieName = column[String]("movie_name")
//    def averageRating = column[Double]("average_rating")
//    def category = column[String]("category")
//    def numRatings = column[Int]("num_ratings")
//    def shortDescription = column[String]("short_description")
//    def longDescription = column[String]("long_description")
//
//    def * = (id, movieName, averageRating, category, numRatings, shortDescription, longDescription)
//      <> ((Movie.apply _).tupled, Movie.unapply)
//  }
//
//  private val movies = TableQuery[MovieTable]
//
//  def findAll(): Future[Seq[Movie]] = db.run {
//    movies.result
//  }
//
//  def findById(id: Int): Future[Option[Movie]] = db.run {
//    movies.filter(_.id === id).result.headOption
//  }
//
//  def findRecommended(): Future[Seq[Movie]] = db.run {
//    movies.filter(_.rating >= 4.0).result
//  }
//
//  def create(movie: Movie): Future[Movie] = db.run {
//    (movies returning movies.map(_.id)
//      into ((movie, id) => movie.copy(id = id))
//      ) += movie
//  }
//
//  def update(id: Int, movie: Movie): Future[Int] = db.run {
//    movies.filter(_.id === id)
//      .map(m => (m.title, m.genre, m.rating))
//      .update((movie.title, movie.genre, movie.rating))
//  }
//
//  def delete(id: Long): Future[Int] = db.run {
//    movies.filter(_.id === id).delete
//  }
//}
