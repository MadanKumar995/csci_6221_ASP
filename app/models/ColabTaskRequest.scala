package models

import play.api.libs.json.{Json, Reads}

// Define this case class in a suitable place, e.g., models/ColabTaskRequest.scala

case class ColabTaskRequest(task: String, subtasks: Seq[String], collaborators: Seq[String])

object ColabTaskRequest {
  implicit val reads: Reads[ColabTaskRequest] = Json.reads[ColabTaskRequest]
}
