package actors

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import actors.BachletActor.SendMessage
import bach.{bb, ag}
import play.api.libs.json._

import models.ItemsModel
import models.Item
import play.api.libs.functional.syntax._

class BachletActor(out: ActorRef) extends Actor {
    import BachletActor._
//    implicit val actionFormat = Json.format[Action]
    def sendItemsTo(actor: ActorRef): Unit = {
        implicit val itemFormat = Json.format[Item];
        actor ! Json.obj("items"->ItemsModel.getItems())
    }

    bb.subject.subscribe(newStore => {
        println("in PUBLISH") 
        sendItemsTo(out)
    })


     def receive = {

    case message: JsValue => 
      // parsing voir https://stackoverflow.com/a/22046047

      implicit val actionReader = (
        (__ \ "action").read[String] and
        (__ \ "itemType").read[String]
//        (__ \ "items").read[List[Map[String, String]]]
)(Action)
        val action =  message.as[Action]

        println(s"FROM JSON, message: $action")

        action match {
            case Action("add", "text") => 
            val item: Item = ItemsModel.addTextItem()
            ag run s"tell(textItem(item${item.id}))"
            case Action("connect", _) => 
            ag run s"tell(user)"
        }
       
    case m => println("Unhandled msg in ChatActor")
     }
}

object BachletActor {
    def props(out: ActorRef) = Props(new BachletActor(out))

    case class SendMessage(msg: String)
    case class Action(actionType: String, itemType: String)
}