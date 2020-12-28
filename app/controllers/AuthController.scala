package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.FakeUserRepository


@Singleton
class AuthController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

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
          if (FakeUserRepository.validateUser(username, password)){
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
