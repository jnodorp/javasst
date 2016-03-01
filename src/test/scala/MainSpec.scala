import java.io.FileDescriptor
import java.net.InetAddress
import java.security.Permission

import org.scalatest.{BeforeAndAfterEach, FlatSpec}

/**
  * Test class for [[Main]].
  */
class MainSpec extends FlatSpec with BeforeAndAfterEach {

  /**
    * The original security manager.
    */
  private val originalSecurityManager: SecurityManager = System.getSecurityManager

  /**
    * The [[NoExitSecurityManager]].
    */
  private var noExitSecurityManager: SecurityManager = null

  override def beforeEach(): Unit = {
    noExitSecurityManager = new NoExitSecurityManager(originalSecurityManager)
    System.setSecurityManager(noExitSecurityManager)
  }

  override def afterEach(): Unit = {
    System.setSecurityManager(originalSecurityManager)
  }

  behavior of "Main"

  it should "exit with exit code 3 if no arguments were given" in {
    assertExit(3, () => Main.main(Array()))
  }

  it should "exit with exit code 3 if too many arguments were given" in {
    assertExit(3, () => Main.main(Array("too", "many", "arguments")))
  }

  it should "exit with exit code 4 if the given file does not exist" in {
    assertExit(4, () => Main.main(Array("NoSuchFile.txt")))
  }

  it should "exit with exit code 5 if the output file is not writable" ignore {
    fail("Not yet implemented.")
  }

  it should "exit with exit code 6 if 'dot' is not available" ignore {
    fail("Not yet implemented.")
  }

  /**
    * Assert that the exit code of the given block tries to shut down the JVM with a given exit code.
    *
    * @param expected The expected exit code.
    * @param actual   The code block to execute.
    */
  private def assertExit(expected: Int, actual: () => Unit): Unit = {
    try {
      actual()
    } catch {
      case e: CheckExitCalled => assert(e.status == expected)
    }
  }

  /**
    * A [[NoExitSecurityManager]] throws a [[CheckExitCalled]] exception whenever [[checkExit(int)]] is called. All other
    * method calls are delegated to the original security manager.
    */
  private class NoExitSecurityManager(val originalSecurityManager: SecurityManager) extends SecurityManager {

    /**
      * The exit code.
      */
    private var statusCode: Option[Int] = Option.empty

    /**
      * Called by <code>System.exit(Int)</code>.
      *
      * @param status The status will be saved.
      * @throws CheckExitCalled Thrown every time this method is called.
      */
    @throws[CheckExitCalled]
    override def checkExit(status: Int) {
      if (statusCode.isEmpty)
        statusCode = Option(status)
      throw new CheckExitCalled(status)
    }

    override def getInCheck: Boolean = {
      (originalSecurityManager != null) && originalSecurityManager.getInCheck
    }

    override def getSecurityContext: Object = {
      if (originalSecurityManager == null)
        super.getSecurityContext
      else
        originalSecurityManager.getSecurityContext
    }

    override def checkPermission(perm: Permission): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPermission(perm)
    }

    override def checkPermission(perm: Permission, context: Object): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPermission(perm, context)
    }

    override def checkCreateClassLoader(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkCreateClassLoader()
    }

    override def checkAccess(t: Thread): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkAccess(t)
    }

    override def checkAccess(g: ThreadGroup): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkAccess(g)
    }

    override def checkExec(cmd: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkExec(cmd)
    }

    override def checkLink(lib: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkLink(lib)
    }

    override def checkRead(fd: FileDescriptor): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkRead(fd)
    }

    override def checkRead(file: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkRead(file)
    }

    override def checkRead(file: String, context: Object): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkRead(file, context)
    }

    override def checkWrite(fd: FileDescriptor): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkWrite(fd)
    }

    override def checkWrite(file: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkWrite(file)
    }

    override def checkDelete(file: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkDelete(file)
    }

    override def checkConnect(host: String, port: Int): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkConnect(host, port)
    }

    override def checkConnect(host: String, port: Int, context: Object): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkConnect(host, port, context)
    }

    override def checkListen(port: Int): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkListen(port)
    }

    override def checkAccept(host: String, port: Int): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkAccept(host, port)
    }

    override def checkMulticast(maddr: InetAddress): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkMulticast(maddr)
    }

    override def checkMulticast(maddr: InetAddress, ttl: Byte): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkMulticast(maddr, ttl)
    }

    override def checkPropertiesAccess(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPropertiesAccess()
    }

    override def checkPropertyAccess(key: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPropertyAccess(key)
    }

    override def checkTopLevelWindow(window: Object): Boolean = {
      if (originalSecurityManager == null)
        super.checkTopLevelWindow(window)
      else
        originalSecurityManager.checkTopLevelWindow(window)
    }

    override def checkPrintJobAccess(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPrintJobAccess()
    }

    override def checkSystemClipboardAccess(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkSystemClipboardAccess()
    }

    override def checkAwtEventQueueAccess(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkAwtEventQueueAccess()
    }

    override def checkPackageAccess(pkg: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPackageAccess(pkg)
    }

    override def checkPackageDefinition(pkg: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkPackageDefinition(pkg)
    }

    override def checkSetFactory(): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkSetFactory()
    }

    override def checkMemberAccess(clazz: Class[_], which: Int): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkMemberAccess(clazz, which)
    }

    override def checkSecurityAccess(target: String): Unit = {
      if (originalSecurityManager != null)
        originalSecurityManager.checkSecurityAccess(target)
    }

    override def getThreadGroup: ThreadGroup = {
      if (originalSecurityManager == null)
        super.getThreadGroup
      else originalSecurityManager.getThreadGroup
    }
  }

  private class CheckExitCalled(val status: Int) extends SecurityException("Tried to exit with status " + status + ".")

}
