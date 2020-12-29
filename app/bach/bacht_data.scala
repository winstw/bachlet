package bach


/* -------------------------------------------------------------------------- 

   Data for the parser and simulator


   AUTHOR : J.-M. Jacquet and D. Darquennes
   DATE   : March 2016
   // Modification commentée
----------------------------------------------------------------------------*/

class Expr
case class bacht_ast_empty_agent() extends Expr
case class bacht_ast_primitive(primitive: String, token: String) extends Expr
// Ajout de primitives avec logique d'association d'un token à un utilisateur
case class bacht_ast_primitive_perm(primitive: String, token: String, user: String) extends Expr
case class bacht_ast_agent(op: String, agenti: Expr, agentii: Expr) extends Expr
