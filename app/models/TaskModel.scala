package models

import anorm._
import play.api.libs.json._


case class TaskModel(
                 id: Int,
                 task: String,
                 status: String,
                 user: String)

object TaskModel {
  implicit def toParameters: ToParameterList[TaskModel] = Macro.toParameters[TaskModel]

  /**
   * Mapping to write a Task out as a JSON value.
   */
  implicit val implicitWrites: Writes[TaskModel] = new Writes[TaskModel] {
    def writes(task: TaskModel): JsValue = {
      Json.obj(
        "id" -> task.id,
        "task" -> task.task,
        "status" -> task.status,
        "user" -> task.user
      )
    }
  }
}

class TaskId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object TaskId {
  def apply(raw: String): TaskId = {
    require(raw != null)
    new TaskId(Integer.parseInt(raw))
  }
}
