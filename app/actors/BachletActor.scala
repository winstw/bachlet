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
        out ! newStore
    })
    var index = 0
    var username = ""
        
    def receive = {
        case userTell: String if userTell.startsWith("tells-user-") => 
            username = userTell.split("-", 4)(2)
            ag run s"tell(user($username))";
            context.become(talking)
        case m => println("Unhandled msg in waiting ChatActor")
    }
    
    def talking: Receive = {
        case s: String => 
            val Array(action: String, predicate: String, user: String, value: String) = s.split("-", 4)
            action match {
                case "gets" => 
                    ag run s"gets($predicate($value),$user)"
                case "tells" => 
                    predicate match {
                        case s: String => ag run s"tells($predicate($value),$user)"
                    case m => println("Unhandled msg in ChatActor : "  +  m);
                    }
            }
        case m => println("Unhandled msg in talking ChatActor")
     }
     
    override def postStop() = {
        println(s"remove $username")
        ag run s"get(user($username))"
    }   
}

object BachletActor {
    def props(out: ActorRef) = Props(new BachletActor(out))
    case class SendMessage(msg: String)
}