package org.jetbrains.sbt
package lang.completion

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.plugins.scala.util.TestUtils
import org.jetbrains.sbt.project.module.SbtModule
import org.jetbrains.sbt.resolvers.SbtIvyResolver
import org.jetbrains.sbt.resolvers.indexes.ResolverIndex
import org.jetbrains.sbt.resolvers.indexes.ResolverIndex.FORCE_UPDATE_KEY

/**
 * @author Nikolay Obedin
 * @since 8/1/14.
 */
class SbtCompletionDependenciesTest extends SbtCompletionTestBase {
  private val root = s"/${TestUtils.getTestDataPath}/sbt/resolvers/testIvyCache"

  override def setUp(): Unit = {
    super.setUp()

    sys.props += FORCE_UPDATE_KEY -> "true"

    val testResolver = new SbtIvyResolver("Test repo", root, isLocal = false)

    testResolver.getIndex(getProjectAdapter).get.doUpdate()

    ModuleManager.getInstance(getProjectAdapter) match {
      case null =>
      case manager =>
        for {
          module <- manager.getModules
        } {
          SbtModule.Resolvers(module) += testResolver
        }
    }
  }

  override def tearDown(): Unit = {
    super.tearDown()
    sys.props -= FORCE_UPDATE_KEY
    FileUtil.delete(ResolverIndex.getIndexDirectory(root))
  }

  def testCompleteGroup(): Unit     = doTest()
  def testCompleteVersion(): Unit   = doTest()
  def testCompleteArtifact(): Unit  = doTest()
}
