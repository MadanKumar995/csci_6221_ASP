package controllers

import models._

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.i18n._
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class LoginData(username: String, password: String)

class LoginController @Inject() (dbservice: APIController, cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
//  class LoginController @Inject()(dbservice: DatabaseModel, protected val dbConfigProvider: DatabaseConfigProvider, val cc: ControllerComponents)(implicit ec: ExecutionContext)
//    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

//  val loginForm: Form[LoginData] = Form(
//    mapping(
//      "username" -> nonEmptyText,
//      "password" -> nonEmptyText
//    )(LoginData.apply)(LoginData.unapply)
//  )
//
//  def showLoginForm = Action { implicit request: MessagesRequest[AnyContent] =>
//    Ok(views.html.login.login(loginForm))
//  }
//
//  def submitLoginForm = Action { implicit request: MessagesRequest[AnyContent] =>
//    loginForm.bindFromRequest.fold(
//      formWithErrors => {
//        BadRequest(views.html.login.login(formWithErrors))
//      },
//      loginData => {
//        Redirect(routes.HomeController.index())
////          .withSession("username" -> loginData.username)
//      }
//    )
//  }


//  private val model = new DatabaseModel(db)

//  def showLoginPage = Action { implicit request =>
//    Ok(views.html.login.login())
//  }

  def showLoginPage = Action { implicit request: MessagesRequest[AnyContent] =>
//    val items = List(1,2,3,4,5,6,7,8,9,0,10)
    Ok(views.html.loginCreate())
  }

  def login = Action { request =>
    val creds = request.body.asFormUrlEncoded
    creds.map { args =>
      val username = args("username").head
      val password = args("password").head
      Redirect(routes.HomeController.showHome())
    }.getOrElse(Redirect(routes.LoginController.showLoginPage))
  }

//  def withJsonBody[A](f: A => Result)(implicit request: Request[AnyContent], reads: Reads[A]) = {
//    request.body.asJson.map { body =>
//      Json.fromJson[A](body) match {
//        case JsSuccess(a, path) => f(a)
//        case e @ JsError(_) => Redirect(routes.LoginController.showLoginPage())
//      }
//    }.getOrElse(Redirect(routes.LoginController.showLoginPage()))
//  }

//  implicit val userDataReads = Json.reads[UserData]

//  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]):  {
//    request.body.asJson.map { body =>
//      Json.fromJson[A](body) match {
//        case JsSuccess(a, path) => f(a)
//        case e@JsError(_) => Future.successful(Redirect(routes.LoginController.showLoginPage))
//      }
//    }.getOrElse(Future.successful(Redirect(routes.LoginController.showLoginPage)))
//  }

//  def validateLogin = Action { implicit request =>
//    request.body.asJson.map { body =>
//      Json.fromJson[UserData](body) match {
//        case JsSuccess(ud, path) =>
//          model.validateUser(ud.username, ud.password).map { userExists =>
//            if (userExists) {
//              Ok("true")
//                .withSession("username" -> ud.username, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
//            }
//            else {
//              Ok("false")
//            }
//          }
//          Ok("false")
//        case e@JsError(_) => Redirect(routes.LoginController.showLoginPage)
//      }
//      Ok("false")
//    }.getOrElse(Redirect(routes.LoginController.showLoginPage))
//
//
////        withJsonBody[UserData] { ud =>
////          if(DatabaseModel.validateUser(ud.username, ud.password)) {
////            Ok(Json.toJson(true))
////              .withSession("username" -> ud.username, "csrfToken" -> play.filters.csrf.CSRF.getToken.map)
////          } else {
////            Ok(Json.toJson(false))
////          }
////        }
//  }

//  def validate = Action.async { implicit request =>
//    withJsonBody[UserData] { ud =>
//      model.validateUser(ud.username, ud.password).map { userExists =>
//        if(userExists) {
//          Ok("Json.toJson(true)")
//            .withSession("username" -> ud.username, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
//        }
//        else {
//          Ok(Json.toJson(false))
//        }
//      }
//    }
//  }

  def createUser() = Action.async(parse.json){ implicit request =>
    val jsonBody = request.body

    val us: String = (jsonBody \ "us").as[String]
    val ps: String = (jsonBody \ "ps").as[String]

    dbservice.createUser(us, ps).map { rowsAffected =>
      if (rowsAffected > 0) {
        Ok(Json.obj("status" -> "success", "message" -> s"Account Created successfully as $us"))
      } else {
        Ok(Json.obj("status" -> "unsuccess", "message" -> s"$us failed to Create Account"))
      }
    }.recover {
      case e: Throwable =>
        InternalServerError(Json.obj("status" -> "error", "message" -> "An error occurred"))
    }

  }


  def validateLogin():Action[JsValue] = Action.async(parse.json){ implicit request =>

    val jsonBody = request.body

//    jsonBodyOption match {
//      case Some(jsonBody) => {
        val us: String = (jsonBody \ "us").as[String]
        val ps: String = (jsonBody \ "ps").as[String]

        // Perform authentication or any other logic here
//        if(dbservice.validateUser(us,ps)){
//          println("okay")
//        }

//        dbservice.validateUser(us,ps).map { dbRes =>
//          println("bool: "+dbRes)

          dbservice.validateUser(us,ps).map {
            case Some((username, dbPassword, frnds)) if dbPassword == ps =>
//              println("frnds from login: ",frnds)
              Ok(Json.obj("status" -> "success", "message" -> s"Logged in successfully as $username"))
                .withSession("username" -> username, "frnds" -> frnds)
            case _ =>
              Ok(Json.obj("status" -> "unsuccess", "message" -> s"$us failed to login"))
          }


//          if(bool == true) {
//            println("OKAY")
////              Ok(views.html.index())
//
////            val csrfTokenOption = play.filters.csrf.CSRF.getToken(request)
////            val csrfToken = csrfTokenOption.map(_.value).getOrElse("")
//
////            Ok(Json.obj("status" -> "success", "message" -> s"Logged in successfully as $us"))
////              .withSession("username" -> us, "csrfToken" -> play.filters.csrf.CSRF.getToken.get.value)
//            Ok(Json.obj("status" -> "success", "message" -> s"Logged in successfully as $us"))
//              .withSession("username" -> us, "frnds" -> frnds )
//          } else{
//            println("NOTTTT OKAY")
////            Ok("NOTTTTT OKAYYY!")
//            Ok(Json.obj("status" -> "Unsuccess", "message" -> s"$us failed to login"))
//          }


//          .recover {
//          case ex: Throwable =>
//            println(s"ERROR BRUH ${ex.getMessage}")
//            InternalServerError(Json.obj("status" -> "error", "message" -> s"Internal server error: ${ex.getMessage}"))
//        }

//        println(taskService.getUser(us, ps))


//      }
//      case None => {
//        BadRequest(Json.obj("status" -> "error", "message" -> "Invalid JSON format"))
//      }
//    }

  }

  def logout = Action { implicit request =>
    // Clear session and respond with a success message
    Ok(Json.obj("status" -> "success", "message" -> "Logged out successfully"))
      .withNewSession
  }


}
