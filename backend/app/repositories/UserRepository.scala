package repositories

import javax.inject._
import models.User
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(implicit ec: ExecutionContext) {
  val db = Database.forConfig("postgres")

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def userId = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("firstname")

    def lastName = column[String]("lastname")

    def login = column[String]("login")

    def password = column[String]("password")

    def * = (userId, firstName, lastName, login, password) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def findPasswordByLogin(login: String): Future[Option[User]] = {
    db.run(users.filter(_.login === login).result.headOption)
  }

  def validateCredentials(login: String, password: String): Future[Boolean] = {
    if (login.length < 8 || password.length < 8) {
      Future.successful(false)
    } else {
      findPasswordByLogin(login).map(_.isEmpty)
    }
  }

  def createUser(firstName: String, lastName: String, login: String, password: String): Future[Unit] = {
    val query = users.map(u => (u.firstName, u.lastName, u.login, u.password))
      .returning(users.map(_.userId))
      .into((userData, userId) => User(userId, userData._1, userData._2, userData._3, userData._4))
      .+=(firstName, lastName, login, password)

    db.run(query).map(_ => ())
  }

}
