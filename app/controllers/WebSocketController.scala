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
import models.ItemsModel
import play.api.mvc.WebSocket.MessageFlowTransformer

@Singleton
class WebSocketController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc){

    def index = Action { implicit request => 
        Ok(views.html.chatPage())

    }
    implicit val transformer = MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, JsValue]
    
    def socket = WebSocket.accept[JsValue, JsValue] { request => 
        println("Getting socket")
        ActorFlow.actorRef  { out => 
        BachletActor.props(out)}
    }
}