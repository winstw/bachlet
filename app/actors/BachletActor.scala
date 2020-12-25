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
        println(s.split('-'))
        val Array(action: String, predicate: String, user: String, value: String) = s.split('-')
        println(action, predicate, user, value)
/*         val action = splitedString(0)
        val predicate = splitedString(1)
 */       

 action match {
            case "gets" => 
                println(s"gets($predicate($value),$user)")
               ag run s"gets($predicate($value),$user)"
            case "tells" => 
                predicate match {
                case "user" => ag run s"tell(user($user))";
                case s: String => 
                    println(s"tells($predicate($value),$user)")
                    ag run s"tells($predicate($value),$user)"
                   index+=1
/*                 case "imageItem" => 
                    ag run "tell(imageItem(" + index + "...." + splitedString(3) + "...." + splitedString(2) + "))"
                    index+=1
                case "videoItem" => 
                    ag run "tell(videoItem(" + index + "...." + splitedString(3) + "...." + splitedString(2) + "))"
                    index+=1
 */            }
/*             case "get" => 
                println("Le truc degeux envoyé : " + "get("+splitedString(1)+"("+splitedString(2).split(" ")(0)+"...."+splitedString(3)+"...."+splitedString(2).split(" ")(1)+"))")
                ag run "get("+splitedString(1)+"("+splitedString(2).split(" ")(0)+"...."+splitedString(3)+"...."+splitedString(2).split(" ")(1)+"))"
 */            case m => println("Unhandled msg in ChatActor : "  +  m);
            }

 

        
      case m => println("Unhandled msg in ChatActor")
     }
}

object BachletActor {
    def props(out: ActorRef) = Props(new BachletActor(out))

    case class SendMessage(msg: String)
}