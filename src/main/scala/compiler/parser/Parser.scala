package compiler.parser

import java.util.NoSuchElementException

import com.typesafe.scalalogging.Logger
import compiler.ast.{Ast, Node}
import compiler.scanner.{Scanner, Token}
import org.slf4j.LoggerFactory

/**
  * A parser converts a scanner to a tree structure.
  *
  * @param scanner The [[compiler.scanner.Scanner]].
  * @tparam T The [[compiler.scanner.Token]] object.
  * @tparam E The [[compiler.scanner.Token]]s type.
  * @tparam O The [[SymbolTable]]s members type.
  * @tparam N The [[compiler.ast.Node]]s type.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
abstract class Parser[T <: Token[E], E <: Enumeration#Value, O <: ParserObject, N <: Node[_, _]](val scanner: Scanner[T, E]) {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * The symbol table.
    */
  var symbolTable: SymbolTable[O, E] = new SymbolTable()

  /**
    * The current token.
    */
  protected var current: T

  /**
    * Start the parsing process (by calling the start node method).
    *
    * @return The generated [[compiler.ast.Ast]].
    */
  def parse: Ast[N]

  /**
    * Set current to the next [[compiler.scanner.Token]].
    */
  protected def next() {
    try {
      this.current = scanner.next()
    } catch {
      case e: NoSuchElementException => logger.error(s"Premature end of file: ${scanner.input.source}.", e)
    }
    if (this.current != null) {
      logger.debug(this.current.toString)
    }
  }

  /**
    * Run the given code with its own [[SymbolTable]].
    *
    * @param f The code to run.
    */
  protected def scope(f: () => Unit) {
    val oldSymbolTable: SymbolTable[O, E] = symbolTable
    symbolTable = new SymbolTable(Option(oldSymbolTable))
    f()
    symbolTable = oldSymbolTable
  }

  /**
    * Switch to the error state.
    *
    * @param expected An expected [[compiler.scanner.Token]].
    */
  protected def error(expected: E): Nothing = {
    error(Seq(expected))
  }

  /**
    * Switch to the error state.
    *
    * @param expected A list of expected tokens.
    */
  protected def error(expected: Seq[E]): Nothing = {
    var message: String = "Unexpected token " + current
    if (expected.nonEmpty) {
      message += ". Expected token of one of the following types: " + expected.mkString(", ") + "."
    }
    logger.error(message)
    throw new RuntimeException(message)
  }

  /**
    * Allow verifications on the current token.
    *
    * @return A [[TokenVerification]] object.
    */
  protected def token(expected: E): TokenVerification = {
    token(Seq(expected))
  }

  /**
    * Allow verifications on the current token.
    *
    * @return A [[TokenVerification]] object.
    */
  protected def token(expected: Seq[E]): TokenVerification = {
    new TokenVerification(expected)
  }

  /**
    * As part of the specification the counter verifies the number of [[compiler.scanner.Token]]s to match.
    *
    * @param expected The expected [[compiler.scanner.Token]]s.
    */
  class TokenVerification(val expected: Seq[E]) {

    /**
      * Make sure the token is available once. Throw an error otherwise.
      */
    def once: T = {
      if (expected.contains(current.typ)) {
        val result: T = current
        next()
        result
      } else {
        error(expected)
      }
    }

    /**
      * If the [[compiler.scanner.Token]] matches the current [[compiler.scanner.Token]] execute the function.
      *
      * @param f The function.
      */
    def optional[U](f: (T) => U) {
      if (expected.contains(current.typ)) {
        f(current)
      }
    }

    /**
      * Repeat the function while the [[compiler.scanner.Token]] matches the current [[compiler.scanner.Token]].
      *
      * @param f The function.
      */
    def repeat[U](f: () => U) {
      while (expected.contains(current.typ)) {
        f()
      }
    }
  }

}
