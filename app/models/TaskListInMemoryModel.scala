package models
import collection.mutable
object TaskListInMemoryModel {
  private val users = mutable.Map[String, String]("john" -> "wayne", "geof" -> "geof", "eleve1" -> "e1", "eleve2" -> "e2", "prof1" -> "p1", "prof2" -> "p2")
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
