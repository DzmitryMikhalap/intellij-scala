package org.jetbrains.plugins.dotty.lang.parser.parsing.expressions

/**
  * @author adkozlov
  */
object Expr1 extends org.jetbrains.plugins.scala.lang.parser.parsing.expressions.Expr1 {
  override protected def block = Block
  override protected def postfixExpr = PostfixExpr
  override protected def expr = Expr
  override protected def enumerators = Enumerators
  override protected def ascription = Ascription
}
