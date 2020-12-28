package bach
import scala.collection.mutable.Map
import rx.lang.scala.subjects.PublishSubject

/* -------------------------------------------------------------------------- 

   The BachT store

   
   AUTHOR : J.-M. Jacquet and D. Darquennes
   DATE   : March 2016

   // Modifications apportés commentées dans le code
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

   // Map permettant de gérer les permissions sur les tokens
   var thePerms = Map[String, String]()

   // Implémentation de la primitive tells dans le store :
   // Ajoute au comportement tell classique l'ajout de l'utilisateur
   // dans la Map des permissions à la clé correspondant au token
   def tells(token: String, user: String): Boolean = {
      println("in tells " + user);
      thePerms = thePerms ++ Map(token -> user)
      this.tell(token);
   }

   // Implémentation de la primitive gets dans le store : 
   // Ne réalise le get "classique" que si l'utilisateur
   // est propriétaire du token OU si l'utilisateur est un professeur
   // et le propriétaire du token n'en est pas un
   def gets(token: String, user: String): Boolean = {
      println("in gets " + user + token);
      thePerms.get(token) match {
         case Some(tokenOwner) => 
         if (user == tokenOwner || (user.startsWith("prof") && !tokenOwner.startsWith("prof"))){
            thePerms.remove(token)
            this.get(token)
         } else false
         case None => false
      }
   }
}

object bb extends BachTStore {

   // Sujet utilisé pour l'émission du nouvel état du store
   // en cas de modifications sur celui-ci (tell et get), voir
   // ci-dessous
   val subject = PublishSubject[String]()

   // Fonction qui crée une représentation du store
   // sous forme lisible par le front-end 
   // (dictionnaire au format JSON)
   def toJson = {

      (for ((token,d) <- theStore) yield 
            thePerms.get(token) match {
            case Some(user: String) => 
            val Array(typ: String, value: String) = token.dropRight(1).split('(');
            s"""{"type": "$typ", "value": "$value", "user": "$user"}"""
            case None => 
            val Array(typ: String, value: String) = token.dropRight(1).split('(');
            s"""{"type": "$typ", "value": "$value"}"""
         }).mkString("[", ",", "]")
      }

   // Génère l'émission du nouvel état du store sur le sujet
   // en cas de tell
   override def tell(token: String): Boolean = {
      val result = super.tell(token)
      subject.onNext(bb.toJson)
      result
   }

   // Génère l'émission du nouvel état du store sur le sujet
   // en cas de get
   override def get(token: String): Boolean = {
      val success = super.get(token)
      if (success){
         subject.onNext(bb.toJson)
      }
      success
   }

   def reset: Unit = { clear_store }

}
