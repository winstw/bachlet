package bach
import scala.collection.mutable.Map
import rx.lang.scala.subjects.PublishSubject

/* -------------------------------------------------------------------------- 

   The BachT store

   
   AUTHOR : J.-M. Jacquet and D. Darquennes
   DATE   : March 2016

----------------------------------------------------------------------------*/

class BachTStore {
   var theStore = Map[String,Int]()
   def tell(token:String):Boolean = {
      if (theStore.contains(token)) 
        { theStore(token) = theStore(token) + 1 }
      else
        { theStore = theStore ++ Map(token -> 1) }
      true
   }


   def ask(token:String):Boolean = {
      if (theStore.contains(token)) 
             if (theStore(token) >= 1) { true }
             else { false }
      else false
   }


   def get(token:String):Boolean = {
      if (theStore.contains(token)) 
             if (theStore(token) >= 1) 
               { theStore(token) = theStore(token) - 1 
               if(theStore(token) == 0){
                    theStore.remove(token)
                 }
                 true 
               }
             else { false }
      else false
   }


   def nask(token:String):Boolean = {
      if (theStore.contains(token)) 
             if (theStore(token) >= 1) { false }
             else { true }
      else true 
   }


   override def toString = 
      (for ((t,d) <- theStore) yield t + "(" + theStore(t) + ")" ).mkString("{", " ", "}")
   



   def print_store: Unit = {
       print("{ \n")
      for ((t,d) <- theStore) 
         print ( t + "(" + theStore(t) + ")\n" )
      println(" }")
    }

   def clear_store: Unit =  {
      theStore = Map[String,Int]()
   }

   var thePerms = Map[String, String]()
   def tells(token: String, user: String): Boolean = {
      println("in tells " + user);
      thePerms = thePerms ++ Map(token -> user)
      this.tell(token);
   }
   def gets(token: String, user: String): Boolean = {
      println("in gets " + user);
      thePerms.get(token) match {
         case Some(tokenOwner) => 
         if (user == tokenOwner || (user.startsWith("prof") && !tokenOwner.startsWith("prof"))){
            thePerms.remove(token)
            return this.get(token)
         } else return false
         case None => 
            return false
      }

   }


}

object bb extends BachTStore {

   val subject = PublishSubject[String]()


   def toJson = 
         (for ((token,d) <- theStore) yield 
         

         thePerms.get(token) match {
            case Some(user: String) => 
            val Array(typ: String, value: String) = token.dropRight(1).split('(');
            s"""{"type": "$typ", "value": "$value", "user": "$user"}"""
            case None => 
            val Array(typ: String, value: String) = token.dropRight(1).split('(');
            s"""{"type": "$typ", "value": "$value"}"""
         }).mkString("[", ",", "]")
         
         

   override def tell(token: String): Boolean = {
      val result = super.tell(token)
      subject.onNext(bb.toJson)
      result
   }
   override def get(token: String): Boolean = {
      val success = super.get(token)
      if (success){
         subject.onNext(bb.toJson)
      }
      success
   }

   
   def reset: Unit = { clear_store }

}
