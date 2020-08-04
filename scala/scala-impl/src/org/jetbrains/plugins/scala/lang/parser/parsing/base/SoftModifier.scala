package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package base

import com.intellij.psi.tree.{IElementType, TokenSet}
import org.jetbrains.plugins.scala.lang.lexer.ScalaModifier._
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenType._
import org.jetbrains.plugins.scala.lang.lexer.{ScalaModifier, ScalaModifierTokenType, ScalaTokenType, ScalaTokenTypes}
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder

// See https://dotty.epfl.ch/docs/reference/soft-modifier.html
sealed abstract class SoftModifier(modifiers: ScalaModifier*) extends ParsingRule {

  import ScalaTokenTypes._

  override def apply()(implicit builder: ScalaPsiBuilder): Boolean = builder.getTokenType match {
    case IsSoftModifier(tokenType) if builder.isScala3 =>
      val marker = builder.mark()

      builder.remapCurrentToken(tokenType)
      builder.advanceLexer() // ate soft modifier

      // soft modifiers must me followed either by:
      // * a hard modifier;
      // * a definition start;
      // * another soft modifier;
      val result = builder.getTokenType match {
        case `kCASE` => isCaseDefinition.contains(builder.lookAhead(0))
        case tokenType if isDefinitionStart.contains(tokenType) ||
          isHardModifier.contains(tokenType) => true
        case IsSoftModifier(_) => isFollowedBySoftModifier()
        case _ => false
      }

      if (result) marker.drop()
      else marker.rollbackTo()

      result
    case _ => false
  }

  private object IsSoftModifier {

    def unapply(tokenType: IElementType)
               (implicit builder: ScalaPsiBuilder): Option[ScalaTokenType] =
      Option(ScalaModifier.byText(builder.getTokenText))
        .filter(modifiers.contains)
        .map(ScalaModifierTokenType(_))
  }

  private def isFollowedBySoftModifier()(implicit builder: ScalaPsiBuilder): Boolean = {
    // check if there is another soft modifier
    val lookAheadMarker = builder.mark()
    try this ()
    finally lookAheadMarker.rollbackTo()
  }

  private val isDefinitionStart = TokenSet.create(
    kDEF,
    kVAL,
    kVAR,
    kTYPE,

    ClassKeyword,
    TraitKeyword,
    ObjectKeyword,
    EnumKeyword,
    GivenKeyword,
  )

  private val isHardModifier = TokenSet.create(
    kPRIVATE,
    kPROTECTED,
    kFINAL,
    kABSTRACT,
    kOVERRIDE,
    kIMPLICIT,
    kSEALED,
    kLAZY,
  )

  private val isCaseDefinition = TokenSet.create(
    ClassKeyword,
    //TraitKeyword,
    ObjectKeyword,
  )
}

/**
 * [[LocalSoftModifier]] ::= 'inline'
 * * | 'transparent'
 * * | 'open'
 */
object LocalSoftModifier extends SoftModifier(
  Inline,
  Transparent,
  Open,
)

/**
 * [[OpaqueModifier]] ::= 'opaque'
 */
object OpaqueModifier extends SoftModifier(
  Opaque
)