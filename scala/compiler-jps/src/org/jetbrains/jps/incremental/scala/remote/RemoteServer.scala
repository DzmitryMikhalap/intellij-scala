package org.jetbrains.jps.incremental.scala
package remote

import java.net.{ConnectException, InetAddress, UnknownHostException}

import org.jetbrains.jps.incremental.ModuleLevelBuilder.ExitCode
import org.jetbrains.jps.incremental.scala.JpsBundle
import org.jetbrains.plugins.scala.compiler.data.{Arguments, CompilationData, CompilerData, SbtData}
import org.jetbrains.plugins.scala.server.CompileServerToken

/**
 * @author Pavel Fatin
 */
class RemoteServer(override val address: InetAddress, override val port: Int)
  extends Server
    with RemoteResourceOwner {

  override def compile(sbtData: SbtData,
                       compilerData: CompilerData,
                       compilationData: CompilationData,
                       client: Client): ExitCode = {

    val arguments = Arguments(sbtData, compilerData, compilationData, None).asStrings

    try {
      val token = CompileServerToken.tokenForPort(port).getOrElse("NO_TOKEN")
      send(CommandIds.Compile, token +: arguments, client)
      // client.compilationEnd() is meant to be sent by remote server
      ExitCode.OK
    } catch {
      case e: ConnectException =>
        val message = cantConnectToCompileServerErrorMessage
        //noinspection ScalaExtractStringToBundle,ReferencePassedToNls
        client.warning(message + "\n" + JpsBundle.message("trying.to.compile.without.it"))
        reportException(message, e, client)
        ScalaBuilder.localServer.compile(sbtData, compilerData, compilationData, client)
      case e: UnknownHostException =>
        val message = unknownHostErrorMessage
        client.error(message)
        reportException(message, e, client)
        ExitCode.ABORT
    }
  }
}