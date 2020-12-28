package bach

/* -------------------------------------------------------------------------- 

   The BachT parser
   
   AUTHOR : J.-M. Jacquet and D. Darquennes
   DATE   : March 2016

   Modifications apportés pour le projet bachlet commentées dans le code
----------------------------------------------------------------------------*/

import scala.util.parsing.combinator._
import scala.util.matching.Regex

class BachTParsers extends RegexParsers {

  def token 	: Parser[String] = ("[0-9a-zA-Z_ /&%=.?:-]*").r ^^ {_.toString}

  val opChoice  : Parser[String] = "+" 
  val opPara    : Parser[String] = "||"
  val opSeq     : Parser[String] = ";" 
 
  def primitive : Parser[Expr]   = "tell("~token~")" ^^ {
        case _ ~ vtoken ~ _  => bacht_ast_primitive("tell",vtoken) }  | 
                                   "ask("~token~")" ^^ {
        case _ ~ vtoken ~ _  => bacht_ast_primitive("ask",vtoken) }   | 
                                   "get("~token~")" ^^ {
        case _ ~ vtoken ~ _  => bacht_ast_primitive("get",vtoken) }   | 
                                   "nask("~token~")" ^^ {
        case _ ~ vtoken ~ _  => bacht_ast_primitive("nask",vtoken) }

  def agent = compositionChoice

  def compositionChoice : Parser[Expr] = compositionPara~rep(opChoice~compositionChoice) ^^ {
        case ag ~ List() => ag
        case agi ~ List(op~agii)  => bacht_ast_agent(op,agi,agii) }

  def compositionPara : Parser[Expr] = compositionSeq~rep(opPara~compositionPara) ^^ {
        case ag ~ List() => ag
        case agi ~ List(op~agii)  => bacht_ast_agent(op,agi,agii) }

  def compositionSeq : Parser[Expr] = simpleAgent~rep(opSeq~compositionSeq) ^^ {
        case ag ~ List() => ag
        case agi ~ List(op~agii)  => bacht_ast_agent(op,agi,agii) }

  def simpleAgent : Parser[Expr] = primitive | parenthesizedAgent

  def parenthesizedAgent : Parser[Expr] = "("~>agent<~")"

}


// Ajout de deux primitives tells et gets au parser original
// ainsi que d'un type de token "predicat" qui prend un parametre entre parentheses
class BachletParsers extends BachTParsers{
      def predicate: Parser[String] = super.token~"("~super.token~")"  ^^ {
            case pred~_~arg~_ => s"$pred($arg)"
       }
      override def token = predicate | super.token
      override def primitive = super.primitive | 
            "tells("~token~","~token~")" ^^ {
        case _ ~ vtoken ~ "," ~ vuser ~ _  => bacht_ast_primitive_perm("tells",vtoken,vuser) } |
            "gets("~token~","~token~")" ^^ {
        case _ ~ vtoken ~ "," ~ vuser ~ _  => bacht_ast_primitive_perm("gets",vtoken,vuser) }

}

// Nouvelle Exception lancée en cas d'erreur de parsing par BachTSimulParser
class ParsingException(message: String) extends RuntimeException(message)

object BachTSimulParser extends BachletParsers {
  // Modifié le type d'exception générée en cas d'erreur de parsing
  def parse_primitive(prim: String) = parseAll(primitive,prim) match {
        case Success(result, _) => result
        case failure : NoSuccess => throw new ParsingException(failure.msg)
  }

  // Modifié le type d'exception générée en cas d'erreur de parsing
  def parse_agent(ag: String) = parseAll(agent,ag) match {
        case Success(result, _) => result
        case failure : NoSuccess => throw new ParsingException(failure.msg)
  }

}

