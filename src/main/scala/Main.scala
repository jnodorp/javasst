import java.io.{File, FileNotFoundException, IOException}
import java.nio.file.{Files, Path}
import javasst.ast.JavaSstNode
import javasst.bytecode.BytecodeGenerator
import javasst.parser.JavaSstParser
import javasst.scanner.JavaSstScanner

import com.typesafe.scalalogging.Logger
import compiler.ast.Ast
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.io.Codec

/**
  * The main class compiles a file (given as a single argument) and displays the [[compiler.ast.Ast]] as a PDF. This
  * requires the installation of the dot renderer (<a href="http://graphviz.org/">http://graphviz.org/</a>) and one of
  * the following PDF viewers:
  * <ul>
  * <li><a href="https://wiki.gnome.org/Apps/Evince">Evince</a></li>
  * <li><a href="https://okular.kde.org/">Okular</a></li>
  * </ul>
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
object Main {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * The [[compiler.ast.Ast]] file.
    */
  private val AST: Path = File.createTempFile("ast", "dot").toPath

  /**
    * The PDF file.
    */
  private val PDF: Path = File.createTempFile("ast", "pdf").toPath

  /**
    * PDF viewers to try.
    */
  private val VIEWERS: Array[String] = Array("evince", "okular")

  /**
    * View the [[compiler.ast.Ast]].
    */
  private val viewAst: Boolean = true

  /**
    * Compile the given source file.
    *
    * @param args The source file.
    */
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      logger.warn("Wrong number of arguments. Expected 1 but was " + args.length + "!")
      System.exit(3)
    }

    var scanner: JavaSstScanner = null
    try {
      scanner = new JavaSstScanner(args(0))
    } catch {
      case e: FileNotFoundException =>
        logger.error(s"Unable to find input file '${args(0)}'.")
        System.exit(4);
    }

    val parser: JavaSstParser = new JavaSstParser(scanner)
    val ast: Ast[JavaSstNode] = parser.parse

    val name: String = new File(args(0)).getName
    val bytecode: BytecodeGenerator = new BytecodeGenerator(name, ast)
    bytecode.generate().writeToFile(bytecode.className + ".class")

    logger.info(s"Successfully compiled file ${args(0)} to ${bytecode.className}.class.")

    if (viewAst) {
      showAst(ast)
    }
  }

  /**
    * Show the [[compiler.ast.Ast]].
    *
    * @param ast The [[compiler.ast.Ast]].
    */
  private def showAst(ast: Ast[JavaSstNode]): Unit = {
    try {
      Files.write(AST, ast.toString.getBytes(Codec.UTF8.charSet))
    } catch {
      case e: IOException =>
        logger.error(s"Unable to save file $AST!", e)
        System.exit(5)
    }

    try {
      run("dot", "-Tps", AST.toString, "-o", PDF.toString)
    } catch {
      case e: IOException =>
        logger.error("You need to install 'dot' to run this program.", e)
        System.exit(6)
    }

    var success: Boolean = false
    for (viewer <- VIEWERS) {
      if (!success) {
        try {
          run(viewer, PDF.toString)
          success = true
        } catch {
          case e: IOException => logger.warn(s"Did not find $viewer!", e)
        }
      }
    }

    if (!success) logger.warn(s"You must install one PDF viewer: $VIEWERS.")
  }

  /**
    * Run a command.
    *
    * @param cmd The command and its parameters.
    * @return The exit code.
    */
  private def run(cmd: String*): Int = {
    new ProcessBuilder(cmd.asJava).inheritIO.start.waitFor
  }
}
