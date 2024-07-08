package controllers

import models.ColabTaskRequest

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class Task(id: Int, task: String, status: String)

@Singleton
class HomeController @Inject()(dbservice:APIController, val cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc)  {
//  class HomeController @Inject()(dbservice: APIController, protected val dbConfigProvider: DatabaseConfigProvider, val cc: ControllerComponents)(implicit ec: ExecutionContext)
//    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

//  import profile.api._

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */



    var us = "Guest"
//  var allTasks: ArrayBuffer[JsObject] = ArrayBuffer.empty[JsObject]

//  def getAllTasks(us: String): Future[Unit] =  {
//    dbservice.getAllTasks(us).map { tasks =>
//      val jsonTasks = tasks.map { case (id, task, status) =>
//        Json.obj(
//          "id" -> id,
//          "task" -> task,
//          "status" -> status
//        )
//      }
//      println("kkkkkkkkk: "+jsonTasks)
//      allTasks = jsonTasks.to(ArrayBuffer)
//      Ok(Json.toJson(jsonTasks))
//    }
////      .recover {
////      case ex: Throwable =>
////        InternalServerError(Json.obj("status" -> "error", "message" -> s"Internal server error: ${ex.getMessage}"))
////    }
//  }

       ///colab create and read
//  def showHome() = Action.async { implicit request: Request[AnyContent] =>
//    val items = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 10)
//    request.session.get("username") match {
//      case Some(username) =>
//        val us = username
//        val frnds = request.session.get("frnds").map(_.split(",").toList).getOrElse(List.empty[String])
//
//        dbservice.getAllTasks(us).flatMap { tasks: Vector[(Int, String, String, String, String, String, String)] =>
//
//          // Function to get the task strings for each task._7
//          def getTaskStrings(taskIds: String): Future[List[String]] = {
//            val ids = taskIds.split(",").filter(_.nonEmpty).toList
//            Future.sequence(ids.map(id => dbservice.getTaskById(id.toInt)))
//          }
//
//          // Create colab tasks with the tasks strings instead of IDs
//          def createColabTasks(tasks: Vector[(Int, String, String, String, String, String, String)], status: String): Future[Vector[(Int, String, String, String, String, String, List[String])]] = {
//            Future.sequence(
//              tasks.filter(task => task._3 == status && task._5 == "colab")
//                .map { task =>
//                  getTaskStrings(task._7).map(taskStrings => (task._1, task._2, task._3, task._4, task._5, task._6, taskStrings))
//                }
//            )
//          }
//
//          for {
//            colabPendingTasks <- createColabTasks(tasks, "pending")
//            colabCompletedTasks <- createColabTasks(tasks, "completed")
//            colabDeletedTasks <- createColabTasks(tasks, "deleted")
//          } yield {
//            val pendingTasks = tasks.filter(task => task._3 == "pending" && task._5 == "indi")
//            val completedTasks = tasks.filter(task => task._3 == "completed" && task._5 == "indi")
//            val deletedTasks = tasks.filter(task => task._3 == "deleted" && task._5 == "indi")
//
//            Ok(views.html.testHome(pendingTasks, completedTasks, deletedTasks, colabPendingTasks, colabCompletedTasks, colabDeletedTasks, us, frnds))
//          }
//        }
//
//      case None =>
//        Future.successful(Redirect(routes.LoginController.showLoginPage))
//    }
//  }


  def showHome() = Action.async { implicit request: Request[AnyContent] =>
    val items = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 10)
    request.session.get("username") match {
      case Some(username) =>
        val us = username
        val frnds = request.session.get("frnds").map(_.split(",").toList).getOrElse(List.empty[String])

        dbservice.getAllTasks(us).flatMap { tasks: Vector[(Int, String, String, String, String, String, String)] =>

          // Function to get the task strings for each task._7 with IDs
          def getTaskStrings(taskIds: String): Future[List[(Int, (String, String))]] = {
            val ids = taskIds.split(",").filter(_.nonEmpty).toList
            Future.sequence(ids.map(id => dbservice.getTaskById(id.toInt).map(task => (id.toInt, task))))
          }

          // Create colab tasks with the tasks strings and IDs instead of IDs
          def createColabTasks(tasks: Vector[(Int, String, String, String, String, String, String)], status: String): Future[Vector[(Int, String, String, String, String, String, List[(Int, (String,String))])]] = {
            Future.sequence(
              tasks.filter(task => task._3 == status && task._5 == "colab")
                .map { task =>
                  getTaskStrings(task._7).map(taskStrings => (task._1, task._2, task._3, task._4, task._5, task._6, taskStrings))
                }
            )
          }

          for {
            colabPendingTasks <- createColabTasks(tasks, "pending")
            colabCompletedTasks <- createColabTasks(tasks, "completed")
            colabDeletedTasks <- createColabTasks(tasks, "deleted")
          } yield {
            val pendingTasks = tasks.filter(task => task._3 == "pending" && task._5 == "indi")
            val completedTasks = tasks.filter(task => task._3 == "completed" && task._5 == "indi")
            val deletedTasks = tasks.filter(task => task._3 == "deleted" && task._5 == "indi")

            Ok(views.html.testHome(pendingTasks, completedTasks, deletedTasks, colabPendingTasks, colabCompletedTasks, colabDeletedTasks, us, frnds))
          }
        }

      case None =>
        Future.successful(Redirect(routes.LoginController.showLoginPage))
    }
  }





//    def showHome() = Action.async { implicit request: Request[AnyContent] =>
////    testDB()
//
//    val items = List(1,2,3,4,5,6,7,8,9,0,10)
//    request.session.get("username") match {
//      case Some(username) =>
//        us = username
//        val frnds = request.session.get("frnds").map(_.split(",").toList).getOrElse(List.empty[String])
////        println("all frnd: " + frnds)
//        dbservice.getAllTasks(us).map { tasks: Vector[(Int, String, String, String, String, String, String)] =>
//          val pendingTasks = tasks.filter(task => task._3 == "pending" && task._5 == "indi")
//          val completedTasks = tasks.filter(task => task._3 == "completed" && task._5 == "indi")
//          val deletedTasks = tasks.filter(task => task._3 == "deleted" && task._5 == "indi")
//
//          val colabPendingTasks = tasks.filter(task => task._3 == "pending" && task._5 == "colab")
//            .map(task => (task._1, task._2, task._3, task._4,task._5, task._6, task._7.split(",").toList))
//          val colabCompletedTasks = tasks.filter(task => task._3 == "completed" && task._5 == "colab")
//            .map(task => (task._1, task._2, task._3, task._4,task._5, task._6, task._7.split(",").toList))
//          val colabDeletedTasks = tasks.filter(task => task._3 == "deleted" && task._5 == "colab")
//            .map(task => (task._1, task._2, task._3, task._4,task._5, task._6, task._7.split(",").toList))
//
//          //      println("TASKS in HOME: "+tasks)
//          //      println(pendingTasks,completedTasks, deletedTasks)
//          Ok(views.html.testHome(pendingTasks, completedTasks, deletedTasks, colabPendingTasks, colabCompletedTasks, colabDeletedTasks, us, frnds))
//        }
//
//
////        getAllTasks(us).map { _ =>
////          println("Username: " + us)
////          println(allTasks)
////          Ok(views.html.testHome(items, us))
////        }.recover {
////          case ex: Throwable =>
////            InternalServerError(Json.obj("status" -> "error", "message" -> s"Internal server error: ${ex.getMessage}"))
////        }
//      case None =>
////        Unauthorized("You are not logged in")
//        Future.successful(Redirect(routes.LoginController.showLoginPage))
//    }
//
////    println("USERRRR: "+us)
////    println(dbservice.getAllTasks())
//
////    Ok(views.html.testHome(items, us))
//  }

  def addTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val taskOpt = (request.body \ "task").asOpt[String]
    val userOpt = (request.body \ "user").asOpt[String]


    request.session.get("username") match {
      case Some(username) =>
        val us = username
        (taskOpt, userOpt) match {
          case (Some(task), Some(user)) =>
            dbservice.addTask(us, task).map { _ =>
              Ok("Task added")
            }
          case _ =>
            Future.successful(BadRequest("Missing task or user"))
        }
      case None =>
        Future.successful(Redirect(routes.LoginController.showLoginPage))
    }

  }


  def updateTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val taskIdOpt = (request.body \ "id").asOpt[String].flatMap(id => scala.util.Try(id.toInt).toOption)
    val taskOpt = (request.body \ "task").asOpt[String]
    println("task + ID : " + taskIdOpt, taskOpt)
    (taskIdOpt, taskOpt) match {
      case (Some(taskId), Some(task)) =>
        dbservice.updateTask(taskId, task).map { _ =>
          Ok("Task added")
        }
      case _ =>
        Future.successful(BadRequest("Missing task or user"))
    }
  }


  def deleteTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val taskIdOpt = (request.body \ "id").asOpt[Int]

    taskIdOpt match {
      case Some(taskId) =>
        dbservice.deleteTask(taskId).map { _ =>
          Ok("Task deleted")
        }
      case None =>
        Future.successful(BadRequest("Missing task ID"))
    }
  }

  def completeTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val taskIdOpt = (request.body \ "id").asOpt[Int]

    taskIdOpt match {
      case Some(taskId) =>
        dbservice.completeTask(taskId).map { _ =>
          Ok("Task Completed")
        }
      case None =>
        Future.successful(BadRequest("Missing task ID"))
    }
  }

//  def notCompleteTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
//    val taskIdOpt = (request.body \ "id").asOpt[Int]
//
//    taskIdOpt match {
//      case Some(taskId) =>
//        dbservice.restoreTask(taskId).map { _ =>
//          Ok("Undo Completed")
//        }
//      case None =>
//        Future.successful(BadRequest("Missing task ID"))
//    }
//  }

  def restoreTask() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val taskIdOpt = (request.body \ "id").asOpt[Int]

    taskIdOpt match {
      case Some(taskId) =>
        dbservice.restoreTask(taskId).map { _ =>
          Ok("Task restored")
        }
      case None =>
        Future.successful(BadRequest("Missing task ID"))
    }
  }


//  def testDB(): Future[Option[(Int, String, String)]] = {
//    val query = sql"select id, username, password from users".as[(Int, String, String)]
//    val queryResult: Future[Vector[(Int, String, String)]] = db.run(query)
//    queryResult.onComplete {
//      case Success(rows) =>
//        println("Query result:")
//        rows.foreach { row =>
//          println(s"ID: ${row._1} UN: ${row._2} PS: ${row._3}")
//        }
//      case Failure(exception) =>
//        println(s"Query failed: ${exception.getMessage}")
//    }
//    queryResult.map(_.headOption)
//  }


  def createTaskWithSubtasksAndCollaborators(us: String, task: String, subtasks: Seq[String], collaborators: Seq[String]) = {
    for {
      subtaskIds <- dbservice.addSubtasks(subtasks, us)
      taskId <- dbservice.addTaskWithSubtasksAndCollaborators(us, task, subtaskIds, collaborators)
    } yield taskId
  }

//  def createColabTask(): Action[JsValue] = Action.async(parse.json) { request =>
//    println("inside ccT: "+request.body)
//    val taskResult = request.body.validate[ColabTaskRequest]
//    taskResult.fold(
//      errors => {
//        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
//      },
//      taskRequest => {
//        val user = taskRequest.user // Assuming user is part of request
//        val task = taskRequest.task
//        val subtasks = taskRequest.subtasks
//        val collaborators = taskRequest.collaborators
//
//        createTaskWithSubtasksAndCollaborators(task, subtasks, collaborators).map { taskId =>
//          Ok(Json.obj("taskId" -> taskId)) // Return task ID or success message
//        }.recover {
//          case e: Exception =>
//            InternalServerError(Json.obj("message" -> s"Failed to create colab task: ${e.getMessage}"))
//        }
//      }
//    )
//  }


  def createColabTask(): Action[AnyContent] = Action.async { implicit request =>
    // Parse the JSON body
    val jsonBody = request.body.asJson.getOrElse(Json.obj())

    // Extract data using pattern matching or directly
    val taskResult = jsonBody.validate[ColabTaskRequest]

    request.session.get("username") match {
      case Some(username) =>
        val us = username
        taskResult.fold(
          errors => {
            Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
          },
          colabTaskRequest => {
            // Extract data directly from validated case class
            val task = colabTaskRequest.task
            val subtasks = colabTaskRequest.subtasks
            val collaborators = colabTaskRequest.collaborators

            //        println("inside ccT: "+task, subtasks, collaborators)

            // Call your service to create the task
            createTaskWithSubtasksAndCollaborators(us, task, subtasks, collaborators).map { taskId =>
              println("wat is taskID: ", taskId)
              Ok(Json.obj("taskId" -> taskId)) // Return task ID or success message
            }.recover {
              case e: Exception =>
                InternalServerError(Json.obj("message" -> s"Failed to create colab task: ${e.getMessage}"))
            }
          }
        )
      case None =>
        Future.successful(Redirect(routes.LoginController.showLoginPage))
    }
  }

  def subDelete(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    // Extract taskId and subTaskId from JSON body
    val taskIdOpt: Option[Int] = (request.body \ "taskId").asOpt[Int]
    val subTaskIdOpt: Option[Int] = (request.body \ "subTaskId").asOpt[Int]

    // Handle the case when both taskId and subTaskId are present
    (taskIdOpt, subTaskIdOpt) match {
      case (Some(taskId), Some(subTaskId)) =>
        // Call the database service method to remove subTaskId from taskId's sub_tasks array
        dbservice.removeSubTaskFromList(taskId, subTaskId).map { _ =>
          Ok("SubTask Deleted")
        }.recover {
          case e: Throwable =>
            // Handle database errors or not found cases
            InternalServerError(s"Failed to delete subtask: ${e.getMessage}")
        }

      // Handle missing taskId or subTaskId in the request
      case (None, _) =>
        Future.successful(BadRequest("Missing task ID"))
      case (_, None) =>
        Future.successful(BadRequest("Missing subtask ID"))
    }
  }


  def changeSubStatus() = Action.async(parse.json) { implicit request: Request[JsValue] =>
    val subTaskIdOpt = (request.body \ "subTaskId").asOpt[Int]
    val statusOpt = (request.body \ "status").asOpt[String]


    (subTaskIdOpt, statusOpt) match {
      case (Some(subTaskId), Some(status)) =>
        dbservice.updateSubTaskStatus(subTaskId, status).map { _ =>
          Ok(Json.obj("success" -> true))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("success" -> false, "message" -> ex.getMessage))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("success" -> false, "message" -> "Invalid input")))
    }
  }


}
