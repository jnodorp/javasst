package javasst

import java.util.concurrent.TimeoutException

import com.typesafe.scalalogging.Logger
import compiler.ast.Node
import org.slf4j.LoggerFactory

/**
  * This class contains functions for semantic analysis. They should be used by applying them to the abstract syntax
  * tree after an otherwise successful compilation.
  *
  * @example
  * {{{
  * val parser: JavaSstParser = new JavaSstParser(scanner)
  * val ast: Ast[JavaSstNode] = parser.parse
  * ast.foreach(Analyzers.allDeclared)
  * }}}
  * @author Julian Schlichtholz
  * @version 1.0.0
  * @todo Add further analyzers.
  */
object Analyzers {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * Checking whether every referenced object is declared somewhere.
    *
    * @return The function.
    */
  def allDeclared: (Node[_, _]) => Unit = {
    node => {
      try {
        node.obj
      } catch {
        case exception: TimeoutException => logger.warn(s"Could not resolve object for ${node.clazz}.")
      }
    }
  }
}
