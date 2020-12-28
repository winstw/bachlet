package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.TaskListInMemoryModel
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def login() = Action { implicit request: Request[AnyContent] =>
        val usernameOption = request.session.get("username")
        usernameOption.map{ username => 
            Redirect(routes.WebSocketController.index())
        }.getOrElse(Ok(views.html.login()))
  }

  def validateLogin() = Action { implicit request => 
        val maybePostVals = request.body.asFormUrlEncoded
        maybePostVals.map {postVals => 
          val username = postVals("username").head
          val password = postVals("password").head
          if (TaskListInMemoryModel.validateUser(username, password)){
                  Redirect(routes.WebSocketController.index()).withSession("username" -> username)
              } else {
                  Redirect(routes.AuthController.login()).flashing("error" -> "Invalid username/password")
                  }
        }
        .getOrElse(Redirect(routes.AuthController.login()))

    }
  
  def logout = Action {
        Redirect(routes.AuthController.login()).withNewSession
  }
}
