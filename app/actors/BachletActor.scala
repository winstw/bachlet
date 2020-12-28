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
    val actionRegex = raw"(tells?|gets?|nask|ask)-(user|imageItem|textItem|videoItem)-([0-9a-zA-Z_ /&%=.?:-]+)".r
       
    def receive = {
        case userTell: String if userTell.startsWith("tells-user-") => 
            username = userTell.split("-", 4)(2)
            ag run s"tell(user($username))";
            context.become(talking)
        case m => println("Unhandled msg in waiting ChatActor")
    }
    
    def talking: Receive = {
        case s: String => s match {
                case command if actionRegex.matches(command) => command match {
                    case actionRegex(action, predicate, value) => 
                        action match {
                            case "gets" => 
                                ag run s"gets($predicate($value),$username)"
                            case "tells" => ag run s"nask($predicate($value));tells($predicate($value),$username)"
                        }
                    }
                case m => println("Unhandled msg in ChatActor : "  +  m);
        }

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