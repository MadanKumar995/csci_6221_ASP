package models

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success}
//import play.api.db.Database

import scala.concurrent.ExecutionContext

class DatabaseModel (db: JdbcProfile#Backend#Database)(implicit ec: ExecutionContext) {

  def validateUser(username: String, password: String): Future[Boolean] = {
    val query = sql"select username, password from users WHERE username = '$username'".as[(String, String)]

    val queryResult: Future[Vector[(String, String)]] = db.run(query)
//    queryResult.onComplete {
//      case Success(rows) =>
//        println("Query result:")
//        rows.foreach { row =>
//          println(s"ID: ${row._1} UN: ${row._2}")
//        }
//      case Failure(exception) =>
//        println(s"Query failed: ${exception.getMessage}")
//    }
    queryResult.map { rows =>
      rows.headOption match {
        case Some((dbUsername, dbPassword)) => dbPassword == password
        case None => false
      }
    }
  }
}