package models
import collection.mutable
object TaskListInMemoryModel {
  private val users = mutable.Map[String, String]("john" -> "wayne", "geof" -> "geof")
  def validateUser(username: String, password: String): Boolean = {
      users.get(username).map(_ == password).getOrElse(false)
  }
  def createUser(username: String, password: String): Boolean = {
      if (users.contains(username)) false else {
          users(username) = password
          true
      }
  }
}
