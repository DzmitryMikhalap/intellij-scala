package org.jetbrains.plugins.scala.lang.parser.parsing.params

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiBuilder

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.lexer.ScalaElementType
import org.jetbrains.plugins.scala.lang.parser.parsing.types.Type
import org.jetbrains.plugins.scala.lang.parser.bnf.BNF
import org.jetbrains.plugins.scala.lang.parser.util.ParserUtils
import org.jetbrains.plugins.scala.util.DebugPrint


/** 
* @author Alexander Podkhalyuzin
* Date: 06.03.2008
*/

/*
 * TypeParamClause ::= '[' VariantTypeParam {',' VariantTypeParam} ']'
 */

object TypeParamClause {
  def parse(builder: PsiBuilder): Boolean = {
    val typeMarker = builder.mark
    builder.getTokenType match {
      case ScalaTokenTypes.tLSQBRACKET => {
        builder.advanceLexer //Ate [
      }
      case _ => {
        typeMarker.drop
        return false
      }
    }
    if (!TypeParam.parse(builder, true)) {
      builder error ScalaBundle.message("wrong.parameter", new Array[Object](0))
    }
    while (builder.getTokenType == ScalaTokenTypes.tCOMMA) {
      builder.advanceLexer //Ate
      if (!TypeParam.parse(builder, true)) {
        builder error ScalaBundle.message("wrong.parameter", new Array[Object](0))
      }
    }
    builder.getTokenType match {
      case ScalaTokenTypes.tRSQBRACKET => {
        builder.advanceLexer //Ate ]
      }
      case _ => {
        builder error ScalaBundle.message("rsqbracket.expected", new Array[Object](0))
      }
    }
    typeMarker.done(ScalaElementTypes.TYPE_PARAM_CLAUSE)
    return true
  }
}