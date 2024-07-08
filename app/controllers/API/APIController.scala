package controllers

import anorm.SqlParser.get
import anorm.{SQL, ~}
import models.TaskModel

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import play.api.db._


@Singleton
//class APIController @Inject()(database: Database , val cc: ControllerComponents)(implicit ec: ExecutionContext)
//  extends AbstractController(cc) {
  class APIController @Inject()( protected val dbConfigProvider: DatabaseConfigProvider, val cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

//  private val DB = database

//  def validateUser(username: String, password: String): Future[Boolean] = {
////    val query = sql"select username, password from users WHERE username = '$username'".as[(String, String)]
//    val query = sql"SELECT username, password FROM users WHERE username=$username".as[(String, String, String)]
//
//    val queryResult: Future[Vector[(String, String, String)]] = db.run(query)
////        queryResult.onComplete {
////          case Success(rows) =>
////            println("CHCK WHERE: "+rows+ "US: "+query.toString)
//////            rows.foreach { row =>
////////              row._2 == password
//////              println(row)
//////            }
////          case Failure(exception) =>
////            println(s"Query failed: ${exception.getMessage}")
////            false
////        }
//    queryResult.map { rows =>
//      rows.headOption match {
//        case Some((_, dbPassword, _)) =>
////          print("CHK PASS: "+dbPassword == password)
//          dbPassword == password
//        case None => false // User not found
//      }
//    }
//  }

  def createUser(username: String, password: String): Future[Int] = {
    val query = sqlu"""INSERT INTO users ("username", "password") VALUES ($username, $password)"""
    db.run(query)
  }

  def validateUser(username: String, password: String): Future[Option[(String, String, String)]] = {
    val query = sql"SELECT username, password, array_to_string(frnds, ',') FROM users WHERE username=$username".as[(String, String, String)]
    db.run(query).map(_.headOption)
}

  val task = {
      get[Int]("id") ~
      get[String]("tasks") ~
      get[String]("status") ~
      get[String]("user") map {
      case id ~ task ~ status ~ user => TaskModel(id, task, status, user)
    }
  }

  def getAllTasks(us: String): Future[Vector[(Int, String, String, String, String, String, String)]] = {
//    val query = sql"SELECT id, tasks, status FROM tasks WHERE user=$us".as[(Int, String, String)]
    val query = sql"SELECT tasks.id, tasks.task, tasks.status, tasks.user, tasks.task_type, array_to_string(tasks.collaborators, ','), array_to_string(sub_tasks, ',') FROM tasks WHERE $us LIKE ANY (tasks.collaborators) OR tasks.user=$us"
      .as[(Int, String, String, String, String, String, String)]
    val queryResult = db.run(query)
//            queryResult.onComplete {
//              case Success(rows) =>
//                println("CHCK WHERE: "+rows.head+"US: "+ queryResult.toString)
//                rows.foreach { row =>
//    //              row._2 == password
//                  println("row?? : "+row)
//                }
//              case Failure(exception) =>
//                println(s"Query failed: ${exception.getMessage}")
//                false
//            }
    queryResult
  }

  def addTask(user: String, task: String): Future[Int] = {
    val query = sqlu"""INSERT INTO tasks ("task", "user", "task_type") VALUES ($task, $user, 'indi')"""
    val queryResult = db.run(query)
    queryResult
  }

  def updateTask(id: Int, task: String): Future[Int] = {
    val query = sqlu"""UPDATE tasks SET task = $task WHERE id = $id"""
    val queryResult = db.run(query)
    queryResult
  }

  def deleteTask(taskId: Int): Future[Int] = {
    val query = sqlu"""UPDATE tasks SET status = 'deleted' WHERE id = $taskId"""
    val queryResult = db.run(query)
    //    queryResult.map { _ =>
    //      Ok("Task deleted")
    //    }.recover {
    //      case ex: Exception =>
    //        InternalServerError(ex.getMessage)
    //    }
    queryResult
  }

  def completeTask(taskId: Int): Future[Int] = {
    val query = sqlu"""UPDATE tasks SET status = 'completed' WHERE id = $taskId"""
    val queryResult = db.run(query)
    queryResult
  }

  def restoreTask(taskId: Int): Future[Int] = {
    val query = sqlu"""UPDATE tasks SET status = 'pending' WHERE id = $taskId"""
    val queryResult = db.run(query)
    queryResult
  }

//  def deleteTask(taskId: Int): Future[Int] = {
//    val query = sqlu"DELETE FROM tasks WHERE id = $taskId"
//    val queryResult = db.run(query)
////    queryResult.map { _ =>
////      Ok("Task deleted")
////    }.recover {
////      case ex: Exception =>
////        InternalServerError(ex.getMessage)
////    }
//    queryResult
//  }

//  def getAllTasks(): List[TaskModel] = DB.withConnection { implicit c =>
//    SQL("select * from tasks").as(task.*) // before editing this was SQL("select * from task").as(task*) - there wasnt a dot between task and *
//  }

//  def addSubtasks(subtasks: Seq[String], us: String): Future[Seq[Int]] = {
//    val idList: Seq[String] = Seq.empty[String]
//    val insertActions = subtasks.map { subtask =>
////      println("each subtask: ", subtask)
//      var query = sql"INSERT INTO tasks (tasks.tasks, tasks.user ,tasks.task_type) VALUES ($subtask, us, 'sub') RETURNING id".as[Int].head
//      val queryResult = db.run(query)
//      idList = idList :+ queryResult.head.toString
//    }
////    db.run(DBIO.sequence(insertActions).transactionally)
//  }

//    def addSubtasks(subtasks: Seq[String], us: String): Future[Seq[Int]] = {
//      var idList: Seq[Int] = Seq.empty[Int]
//
//      val insertActions = subtasks.map { subtask =>
//          val query = sqlu"""INSERT INTO tasks (task, "user", task_type) VALUES ($subtask, $us, 'sub') RETURNING id"""
//        val queryResult: Future[Int] = db.run(query)
//
//        queryResult.flatMap { taskId =>
//          idList = idList :+ taskId
//          Future.successful(taskId)
//        }
//      }
//
//      println(Future.sequence(insertActions).map(_ => idList))
//      Future.sequence(insertActions).map(_ => idList)
//    }

  def addSubtasks(subtasks: Seq[String], us: String): Future[Seq[Int]] = {
    val insertActions = DBIO.sequence(subtasks.map { subtask =>
      // Using insert and returning to get the generated ID
      (sqlu"""INSERT INTO tasks (task, "user", task_type) VALUES ($subtask, $us, 'sub')""")
        .andThen(sql"SELECT lastval()".as[Int].head)
    })

    // Running the sequence of actions and mapping the result to retrieve IDs
    db.run(insertActions)
  }

//  def addTaskWithSubtasksAndCollaborators(user: String, task: String, subtaskIds: Seq[Int], collaborators: Seq[String]) = {
//
//    val subtaskIdsArray = subtaskIds.mkString("{", ",", "}")
//    val collaboratorsArray = collaborators.mkString("{", ",", "}")
//    val query =
//      sqlu"""INSERT INTO tasks ("task", "user", "task_type" ,"collaborators", "sub_tasks") VALUES ($task, $user, 'colab', $collaboratorsArray, $subtaskIdsArray)"""
//    val queryResult = db.run(query)
//    queryResult.onComplete {
//                    case Success(rows) =>
//                      println(rows)
//                    case Failure(exception) =>
//                      println(s"Query failed: ${exception.getMessage}")
//                      0
//                  }
//    queryResult
//  }

  def addTaskWithSubtasksAndCollaborators(user: String, task: String, subtaskIds: Seq[Int], collaborators: Seq[String]): Future[Int] = {
    // Convert subtaskIds to PostgreSQL array format

    val subtaskIdsArray = "{" + subtaskIds.mkString(",") + "}"

    // Convert collaborators to PostgreSQL array format with proper quoting
    val collaboratorsArray = "{" + collaborators.map(c => c).mkString(",") + "}"

    val query =
      sqlu"""
          INSERT INTO tasks ("task", "user", "task_type", "collaborators", "sub_tasks")
          VALUES ($task, $user, 'colab', $collaboratorsArray::character varying[], $subtaskIdsArray::integer[])
        """

    val queryResult: Future[Int] = db.run(query)
    queryResult
  }

  def getTaskById(taskId: Int): Future[(String,String)] = {
    val query = sql"""SELECT task, tasks.status FROM tasks WHERE id = $taskId""".as[(String,String)].headOption
    db.run(query).map {
      case Some(task) => task
      case None => throw new NoSuchElementException(s"Task with id $taskId not found")
    }
  }

  def subDelete(taskId: Int): Future[Int] = {
    val query = sqlu"""DELETE FROM tasks WHERE id=$taskId"""
    val queryResult = db.run(query)
    queryResult
  }

  def removeSubTaskFromList(taskId: Int, subTaskId: Int): Future[Unit] = {

    subDelete(subTaskId)

    val query =
      sqlu"""
      UPDATE tasks
      SET sub_tasks = array_remove(sub_tasks, $subTaskId)
      WHERE id = $taskId
    """
    db.run(query).map(_ => ())
  }

  def updateSubTaskStatus(subTaskId: Int, status: String): Future[Int] = {
    val query = sqlu"UPDATE tasks SET status = $status WHERE id = $subTaskId"
    var queryResult = db.run(query)

    queryResult
  }

}