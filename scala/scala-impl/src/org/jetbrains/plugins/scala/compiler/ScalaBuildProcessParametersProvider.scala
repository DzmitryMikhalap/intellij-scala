package org.jetbrains.plugins.scala.compiler

import java.util

import com.intellij.compiler.server.BuildProcessParametersProvider
import com.intellij.openapi.project.Project
import org.jetbrains.jps.api.GlobalOptions
import org.jetbrains.plugins.scala.compiler.data.SbtData
import org.jetbrains.plugins.scala.externalHighlighters.ScalaHighlightingMode

import scala.collection.JavaConverters._

/**
  * @author Nikolay.Tropin
  */
class ScalaBuildProcessParametersProvider(project: Project)
  extends BuildProcessParametersProvider {
  
  override def getVMArguments: util.List[String] = {
    customScalaCompilerInterfaceDir().toSeq ++
    compileParallelMaxThreadsOption().toSeq
  }.asJava

  private def customScalaCompilerInterfaceDir(): Option[String] = {
    val key = SbtData.compilerInterfacesKey
    val custom = Option(System.getProperty(key))
    custom.map(path => s"-D$key=$path")
  }
  
  private def compileParallelMaxThreadsOption(): Option[String] = {
    val key = GlobalOptions.COMPILE_PARALLEL_MAX_THREADS_OPTION
    val value = ScalaCompileServerSettings.getInstance.COMPILE_SERVER_PARALLELISM
    Some(s"-D$key=$value")
  }

  override def isProcessPreloadingEnabled: Boolean =
    !ScalaHighlightingMode.isShowErrorsFromCompilerEnabled(project)
}
