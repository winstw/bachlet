package bach

/* -------------------------------------------------------------------------- 

   BachT simulator


   AUTHOR : J.-M. Jacquet and D. Darquennes
   DATE   : March 2016

----------------------------------------------------------------------------*/

import scala.util.Random
import language.postfixOps

class BachTSimul(var bb: BachTStore) {

   val bacht_random_choice = new Random()

   def run_one(agent: Expr):(Boolean,Expr) = {

      agent match {
         case bacht_ast_primitive(prim,token) => 
            {  if (exec_primitive(prim,token)) { (true,bacht_ast_empty_agent()) }
               else { (false,agent) }
            }

         case bacht_ast_primitive_perm(prim,token, user) => 
            {  if (exec_primitive_perm(prim,token, user)) { (true,bacht_ast_empty_agent()) }
               else { (false,agent) }
            }


         case bacht_ast_agent(";",ag_i,ag_ii) =>
            {  run_one(ag_i) match
                  { case (false,_) => (false,agent)
                    case (true,bacht_ast_empty_agent()) => (true,ag_ii)                 
                    case (true,ag_cont) => (true,bacht_ast_agent(";",ag_cont,ag_ii))
                  }
            }

         case bacht_ast_agent("||",ag_i,ag_ii) =>
            {  var branch_choice = bacht_random_choice.nextInt(2) 
               if (branch_choice == 0) 
                 { run_one( ag_i ) match
                      { case (false,_) => 
                          { run_one( ag_ii ) match
                              { case (false,_)
                                  => (false,agent)
                                case (true,bacht_ast_empty_agent())
                                  => (true,ag_i)                 
                                case (true,ag_cont)
                                  => (true,bacht_ast_agent("||",ag_i,ag_cont))
                              }
                          }
                        case (true,bacht_ast_empty_agent())
                                  => (true,ag_ii)                 
                        case (true,ag_cont) 
                                  => (true,bacht_ast_agent("||",ag_cont,ag_ii))
                      }
                  }
               else
                 { run_one( ag_ii ) match
                      { case (false,_) => 
                          { run_one( ag_i ) match
                              { case (false,_) => (false,agent)
                                case (true,bacht_ast_empty_agent()) => (true,ag_ii)                 
                                case (true,ag_cont)
                                      => (true,bacht_ast_agent("||",ag_cont,ag_ii))
                              }
                          }
                        case (true,bacht_ast_empty_agent()) 
                          => (true,ag_i)                 
                        case (true,ag_cont)
                          => (true,bacht_ast_agent("||",ag_i,ag_cont))
                      }
                  }
             
            }


         case bacht_ast_agent("+",ag_i,ag_ii) =>
            {  var branch_choice = bacht_random_choice.nextInt(2) 
               if (branch_choice == 0) 
                 { run_one( ag_i ) match
                      { case (false,_) => 
                          { run_one( ag_ii ) match
                              { case (false,_) => (false,agent)
                                case (true,bacht_ast_empty_agent())
                                  => (true,bacht_ast_empty_agent())                 
                                case (true,ag_cont)
                                  => (true,ag_cont)
                              }
                          }
                        case (true,bacht_ast_empty_agent())
                          => (true,bacht_ast_empty_agent())                 
                        case (true,ag_cont) 
                          => (true,ag_cont)
                      }
                  }
               else
                 { run_one( ag_ii ) match
                      { case (false,_) => 
                          { run_one( ag_i ) match
                              { case (false,_) 
                                  => (false,agent)
                                case (true,bacht_ast_empty_agent())
                                  => (true,bacht_ast_empty_agent())                 
                                case (true,ag_cont)
                                  => (true,ag_cont)
                              }
                          }
                        case (true,bacht_ast_empty_agent())
                          => (true,bacht_ast_empty_agent())                 
                        case (true,ag_cont)
                          => (true,ag_cont)
                      }
                  }
            }
      }
   }

   def bacht_exec_all(agent: Expr):Boolean = {

       var failure = false
       var c_agent = agent
       while ( c_agent != bacht_ast_empty_agent() && !failure ) {
          failure = run_one(c_agent) match 
               { case (false,_)          => true
                 case (true,new_agent)  => 
                    { c_agent = new_agent
                      false
                    }
               }
           bb.print_store
           println("\n")
       }
       
       if (c_agent == bacht_ast_empty_agent()) {
           println("Success\n")
           true
       }
       else {
           println("failure\n")
           false
       }
   }  

   def bacht_runner(agent: Expr): Expr = {

       var failure = false
       var c_agent = agent
       while ( c_agent != bacht_ast_empty_agent() && !failure ) {
          failure = run_one(c_agent) match 
               { case (false,_)          => true
                 case (true,new_agent)  => 
                    { c_agent = new_agent
                      false
                    }
               }
           bb.print_store
           println("\n")
       }
       
       if (c_agent == bacht_ast_empty_agent()) {
           println("Success\n")
       }
       else {
           println("failure\n")
       }
       c_agent
   }  


   def exec_primitive(prim:String,token:String):Boolean = {
       prim match
         { case "tell" => bb.tell(token)
           case "ask"  => bb.ask(token)
           case "get"  => bb.get(token)
           case "nask" => bb.nask(token)
         }
   }
     def exec_primitive_perm(prim:String,token:String,user:String):Boolean = {
       prim match
         { case "tells" => bb.tells(token,user)
           case "ask"  => bb.ask(token)
           case "gets"  => bb.gets(token, user)
           case "nask" => bb.nask(token)
         }
   }
}


object ag extends BachTSimul(bb) {

  def apply(agent: String): Boolean = {
    try {
        val agent_parsed = BachTSimulParser.parse_agent(agent)
        ag.bacht_exec_all(agent_parsed) 
    } catch {
        case error: Throwable => {
          println(s"BachTSimul error : $error")
          false
        }
    }
  }
  def eval(agent:String):Boolean = { apply(agent) }
  def run(agent:String):Boolean =  { apply(agent) }

}
         
