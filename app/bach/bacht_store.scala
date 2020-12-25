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



   def print_store {
      println(this.toString())

       print("{ ")
      for ((t,d) <- theStore) 
         print ( t + "(" + theStore(t) + ")" )
      println(" }")
    }

   def clear_store: Unit =  {
      theStore = Map[String,Int]()
   }

}

object bb extends BachTStore {
   val subject = PublishSubject[String]()

   override def tell(token: String): Boolean = {
      val result = super.tell(token)
      subject.onNext(bb.toString())
      result
   }
   override def get(token: String): Boolean = {
      val success = super.get(token)
      if (success){
         subject.onNext(bb.toString())
      }
      success
   }
   
   def reset { clear_store }

}
