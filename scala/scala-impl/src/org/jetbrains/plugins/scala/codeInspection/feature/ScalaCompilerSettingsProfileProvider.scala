package org.jetbrains.plugins.scala.codeInspection.feature

import com.intellij.psi.PsiFile
import org.jetbrains.plugins.scala.ExtensionPointDeclaration
import org.jetbrains.plugins.scala.extensions.IteratorExt
import org.jetbrains.plugins.scala.project.settings.ScalaCompilerSettingsProfile

trait ScalaCompilerSettingsProfileProvider {

  def provide(file: PsiFile): Option[ScalaCompilerSettingsProfile]
}

object ScalaCompilerSettingsProfileProvider
  extends ExtensionPointDeclaration[ScalaCompilerSettingsProfileProvider](
    "org.intellij.scala.compilerSettingsProfileProvider"
  ) {

  def settingsFor(file: PsiFile): Option[ScalaCompilerSettingsProfile] =
    implementations.iterator.flatMap(_.provide(file)).headOption
}
