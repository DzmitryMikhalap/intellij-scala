package org.jetbrains.plugins.scala.lang.parser.parsing

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.lexer.ScalaElementType
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.parser.util.ParserUtils
import org.jetbrains.plugins.scala.util.DebugPrint
import org.jetbrains.plugins.scala.lang.parser.parsing.types.StableId
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.TokenSet
import org.jetbrains.plugins.scala.ScalaBundle

/**
* @author Alexander Podkhalyuzin
* Date: 05.02.2008
*/

/*
*  TopStatSeq ::= TopStat {semi TopStat}
*/

object TopStatSeq {
  def parse(builder: PsiBuilder): Int = parse(builder, false)
  def parse(builder: PsiBuilder, waitBrace: Boolean): Int = parse(builder, waitBrace, false)
  def parse(builder: PsiBuilder, waitBrace: Boolean, hasPackage: Boolean): Int = {
    var parseState = ParserState.FILE_STATE //ParserState.EMPTY_STATE
    if (waitBrace) parseState = ParserState.FILE_STATE
    if(hasPackage) parseState = ParserState.FILE_STATE
    while (true) {
      builder.getTokenType match {
        //end of parsing when find } or builder.eof
        case ScalaTokenTypes.tRBRACE if waitBrace => return parseState
        case null => return parseState
        case ScalaTokenTypes.tLINE_TERMINATOR |
             ScalaTokenTypes.tSEMICOLON => builder.advanceLexer //not interesting case
        //otherwise parse TopStat
        case _ => {
          def error = {
            builder error ScalaBundle.message("wrong.top.statment.declaration")
            builder.advanceLexer
          }
          (parseState, TopStat.parse(builder, parseState)) match {
            case (_, 0) => error
            case (0, i) => {
              parseState = i % 3
              builder.getTokenType match {
                case ScalaTokenTypes.tSEMICOLON |
                        ScalaTokenTypes.tLINE_TERMINATOR => builder.advanceLexer //it is good
                case ScalaTokenTypes.tRBRACE if waitBrace => return parseState
                case null => return parseState
                case _ => builder error ScalaBundle.message("semi.expected")
              }
            }
            case _ => {
              builder.getTokenType match {
                case ScalaTokenTypes.tSEMICOLON |
                        ScalaTokenTypes.tLINE_TERMINATOR => builder.advanceLexer //it is good
                case ScalaTokenTypes.tRBRACE if waitBrace => return parseState
                case null => return parseState
                case _ => builder error ScalaBundle.message("semi.expected")
              }
            }
          }
        }
      }
    }
    return parseState
  }
}