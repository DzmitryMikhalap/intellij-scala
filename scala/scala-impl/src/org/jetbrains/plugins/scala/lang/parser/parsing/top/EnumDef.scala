package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package top

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder
import org.jetbrains.plugins.scala.lang.parser.parsing.params.ClassConstr

/**
 * [[EnumDef]] ::= id [[ClassConstr]] InheritClauses EnumBody
 */
object EnumDef extends ParsingRule {

  override def apply()(implicit builder: ScalaPsiBuilder): Boolean =
    builder.getTokenType match {
      case ScalaTokenTypes.tIDENTIFIER =>
        builder.advanceLexer()
        ClassConstr()
        // todo InheritClauses EnumBody
        true
      case _ =>
        builder.error(ScalaBundle.message("identifier.expected"))
        false
    }
}