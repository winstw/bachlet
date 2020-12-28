package controllers

import javax.inject._
import play.api.mvc._
import play.api.i18n._
import play.api.libs.json._
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import actors.BachletActor
import akka.actor.Props
import akka.actor.ActorRef

@Singleton
class WebSocketController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc){

    def index = Action { implicit request => 
        val usernameOption = request.session.get("username")
        usernameOption.map{ username =>
            Ok(views.html.padlet(username))
        }.getOrElse(Redirect(routes.AuthController.login()))
    }

    def socket = WebSocket.accept[String, String] { request => 
        println("Getting socket")
        ActorFlow.actorRef  { out => 
        BachletActor.props(out)}
    }
}