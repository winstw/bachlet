package actors

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import actors.BachletActor.SendMessage
import bach.{bb, ag}

class BachletActor(out: ActorRef) extends Actor {
        bb.subject.subscribe(newStore => {
    println("in PUBLISH") 
        out ! newStore
    })

/*     manager ! ChatManager.NewChatter(self)
 */    def receive = {
  case s: String => 
  println(s)
        ag run s
/*         for (c <- chatters) c ! ChatActor.SendMessage(bb.toString())
 */
/*         case "get" => 
        ag run s"get(new);tell(got)"
        case "tell" => 
        ag run s"tell(new)"

 *//*         case s: String => manager ! ChatManager.Message(s)
 *//*         case SendMessage(msg) => out ! msg
    */      case m => println("Unhandled msg in ChatActor")
     }
}

object BachletActor {
    def props(out: ActorRef) = Props(new BachletActor(out))

    case class SendMessage(msg: String)
}