package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package base

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes.kOVERRIDE
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder

/**
 * [[Modifier]] ::= 'override'
 * | [[OpaqueModifier]]
 * | [[LocalModifier]]
 * | [[AccessModifier]]
 *
 * @author Alexander Podkhalyuzin
 *         Date: 15.02.2008
 */
object Modifier extends ParsingRule {

  override def apply()(implicit builder: ScalaPsiBuilder): Boolean = builder.getTokenType match {
    case `kOVERRIDE` =>
      builder.advanceLexer() // Ate override
      true
    case _ =>
      OpaqueModifier() ||
        LocalModifier() ||
        AccessModifier()
  }
}