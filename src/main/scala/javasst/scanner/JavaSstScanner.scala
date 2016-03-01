package javasst.scanner

import java.util.InputMismatchException
import javasst.JavaSstType._

import com.typesafe.scalalogging.Logger
import compiler.scanner.Scanner
import org.slf4j.LoggerFactory

import scala.util.matching.Regex

/**
  * This class processes input provided by a source file.
  *
  * @param source The source file.
  */
class JavaSstScanner(source: String) extends Scanner[JavaSstToken, JavaSstType](source) {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * A pattern matching digits.
    */
  private val DIGIT: Regex = new Regex("[0-9]")

  /**
    * A pattern matching letters.
    */
  private val LETTER: Regex = new Regex("[a-zA-Z]")

  /**
    * A pattern matching whitespaces.
    */
  private val WHITESPACE: Regex = new Regex("[\\s]")

  /**
    * A pattern matching whitespaces or opening parenthesis.
    */
  private val WHITESPACE_OR_PARENTHESIS: Regex = new Regex("[\\s(]")

  /**
    * EOF character for matching.
    */
  private val eof: Char = (-1).toChar

  /**
    * If the currently read characters are in a comment.
    */
  private var comment: Boolean = false

  @throws(classOf[InputMismatchException])
  @throws(classOf[NoSuchElementException])
  override def next(): JavaSstToken = {
    stack = ""

    var symbol: JavaSstType = null
    while (current <= ' ') {
      var whitespace: String = (current + "").replaceAll("\n", "\\\\n")
      whitespace = whitespace.replaceAll("\r", "\\\\r")
      logger.debug("Skipping whitespace '" + whitespace + "'.")
      if (input.hasNext) current = input.next() else throw new NoSuchElementException
    }

    stack += current
    logger.debug("Matching character '" + current + "'.")
    val line: Int = input.y
    val column: Int = input.x
    current match {
      case '{' =>
        symbol = CURLY_BRACE_OPEN
      case '}' =>
        symbol = CURLY_BRACE_CLOSE
      case ';' =>
        symbol = SEMICOLON
      case '(' =>
        symbol = PARENTHESIS_OPEN
      case ')' =>
        symbol = PARENTHESIS_CLOSE
      case ',' =>
        symbol = COMMA
      case '+' =>
        symbol = PLUS
      case '-' =>
        symbol = MINUS
      case '*' =>
        symbol = lookahead("*/", COMMENT_STOP, TIMES)
      case '/' =>
        symbol = lookahead("/*", COMMENT_START, SLASH)
      case '<' =>
        symbol = lookahead("<=", LESS_THAN_EQUALS, LESS_THAN)
      case '>' =>
        symbol = lookahead(">=", GREATER_THAN_EQUALS, GREATER_THAN)
      case '=' =>
        symbol = lookahead("==", EQUALS_EQUALS, EQUALS)
      case 'c' =>
        symbol = lookahead("class", CLASS, null, WHITESPACE)
      case 'e' =>
        symbol = lookahead("else", ELSE, null)
      case 'f' =>
        symbol = lookahead("final", FINAL, null, WHITESPACE)
      case 'i' =>
        if (lookahead("if", IF, null, WHITESPACE_OR_PARENTHESIS) != null) {
          symbol = IF
        }
        else if (lookahead("int", INTEGER, null, WHITESPACE) != null) {
          symbol = INTEGER
        }
        else {
          symbol = null
        }
      case 'p' =>
        symbol = lookahead("public", PUBLIC, null, WHITESPACE)
      case 'r' =>
        symbol = lookahead("return", RETURN, null, WHITESPACE)
      case 'v' =>
        symbol = lookahead("void", VOID, null, WHITESPACE)
      case 'w' =>
        symbol = lookahead("while", WHILE, null, WHITESPACE_OR_PARENTHESIS)
      case `eof` =>
        symbol = EOF
      case _ =>
        if (DIGIT.pattern.matcher("" + current).matches) {
          while (DIGIT.pattern.matcher("" + current).matches) {
            current = input.next()
            stack += current
            symbol = NUMBER
          }
        }
    }

    if (symbol == null && LETTER.pattern.matcher("" + current).matches) {
      while (LETTER.pattern.matcher("" + current).matches
        || DIGIT.pattern.matcher("" + current).matches) {
        current = input.next()
        stack += current
        symbol = IDENT
      }
    }

    if (symbol == IDENT || symbol == NUMBER) {
      stack = stack.substring(0, stack.length - 1)
    } else {
      if (!input.hasNext) {
        current = (-1).toChar
      } else {
        current = input.next()
      }
    }

    if (symbol eq COMMENT_START) {
      comment = true
      return next()
    }

    if (symbol eq COMMENT_STOP) {
      comment = false
      return next()
    }

    if (comment) {
      return next()
    }

    if (symbol == null) {
      throw new InputMismatchException("Invalid input '" + stack + "' at " + "[" + line + ":" + (column - 1) + "]@" + input.source)
    }

    logger.debug("Found token '" + symbol + "' with stack '" + stack + "'.")

    new JavaSstToken(stack, symbol, line, column - 1, input.source)
  }
}
