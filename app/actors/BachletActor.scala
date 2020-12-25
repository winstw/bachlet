package actors

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import actors.BachletActor.SendMessage
import bach.{bb, ag}
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._

class BachletActor(out: ActorRef) extends Actor {
        bb.subject.subscribe(newStore => {
        println("in PUBLISH : " + newStore)
        out ! newStore
    })
    var index = 0
    def receive = {
    case s: String => 
        println(s)
        val splitedString = s.split("-", 4)

        splitedString(0) match {
            case "tell" => 
                splitedString(1) match {
                case "textItem" => 
                    ag run "tell(textItem(" + index + "...." + splitedString(3) + "...." + splitedString(2) + "))"
                    index+=1
                case "imageItem" => 
                    ag run "tell(imageItem(" + index + "...." + splitedString(3) + "...." + splitedString(2) + "))"
                    index+=1
                case "videoItem" => 
                    ag run "tell(videoItem(" + index + "...." + splitedString(3) + "...." + splitedString(2) + "))"
                    index+=1
            }
            case "get" => 
                println("Le truc degeux envoyé : " + "get("+splitedString(1)+"("+splitedString(2).split(" ")(0)+"...."+splitedString(3)+"...."+splitedString(2).split(" ")(1)+"))")
                ag run "get("+splitedString(1)+"("+splitedString(2).split(" ")(0)+"...."+splitedString(3)+"...."+splitedString(2).split(" ")(1)+"))"
        }

        
      case m => println("Unhandled msg in ChatActor")
     }
}

object BachletActor {
    def props(out: ActorRef) = Props(new BachletActor(out))

    case class SendMessage(msg: String)
}