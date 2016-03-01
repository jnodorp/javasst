package compiler.scanner

import java.io.{FileInputStream, InputStream}
import java.util.InputMismatchException

import scala.util.matching.Regex

/**
  * This class processes input provided by an iterator over characters.
  *
  * @param iterator The iterator.
  * @param source   The source descriptor.
  * @tparam T The [[Token]].
  * @tparam E The [[Token]]s type.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
abstract class Scanner[T <: Token[E], E <: Enumeration#Value](private val iterator: Iterator[Char], val source: String = "") {

  /**
    * The input.
    */
  val input: Iterator[Char] with LookAhead[Char] with Position = new LookAheadInput(iterator, source)

  /**
    * The last read character.
    */
  protected var current: Char = _

  /**
    * The read characters since the last token.
    */
  protected var stack: String = _

  /**
    * Create a new [[Scanner]] by converting an input stream.
    *
    * @param inputStream The input stream.
    * @param source      The source file.
    */
  def this(inputStream: InputStream, source: String) {
    this(Iterator.continually(inputStream.read()).takeWhile(-1 != _).map(_.toChar), source)
  }

  /**
    * Create a new [[Scanner]] by reading a file.
    *
    * @param source The source file.
    */
  def this(source: String) {
    this(new FileInputStream(source), source)
  }

  /**
    * Get the next [[Token]].
    *
    * @throws InputMismatchException Thrown if the input is invalid.
    * @throws NoSuchElementException Thrown if there are no tokens left.
    * @return The next [[Token]].
    */
  @throws(classOf[InputMismatchException])
  @throws(classOf[NoSuchElementException])
  def next(): T

  /**
    * Perform a lookahead.
    *
    * @param matched The characters to look at.
    * @param success [[Token]] type to return on match.
    * @param failure [[Token]] type to return on mismatch.
    * @param endedBy A pattern matching a valid right delimiter for this lookahead.
    * @return One of the specified [[Token]] types.
    */
  def lookahead(matched: String, success: E, failure: E, endedBy: Regex = null): E = {
    val newMatch = matched.substring(1)
    val lookaheadBuilder: StringBuilder = new StringBuilder()
    input.lookAhead(newMatch.getBytes.length + 1).foreach(lookaheadBuilder.append)
    val lookahead = lookaheadBuilder.toString().substring(0, lookaheadBuilder.length - 1)
    val next = lookaheadBuilder.toString().substring(lookahead.length, lookaheadBuilder.length)

    val delimited: Boolean = endedBy == null || endedBy.pattern.matcher(next).matches()
    if (newMatch.equals(lookahead) && delimited) {
      stack = matched
      for (i <- 0 until newMatch.getBytes.length) {
        input.next()
      }
      success
    } else {
      failure
    }
  }

  /**
    * This class provides an implementation of an [[Iterator]] over [[Char]]s with [[LookAhead]] and [[Position]] trait
    * mixed in.
    *
    * @param iterator The underlying [[Iterator]].
    * @param source   The source descriptor.
    */
  private class LookAheadInput(private var iterator: Iterator[Char], val source: String = "InputStream")
    extends Iterator[Char] with LookAhead[Char] with Position {

    /**
      * The current line.
      */
    private var _line: Int = 1

    /**
      * The current column.
      */
    private var _column: Int = 1

    /**
      * @inheritdoc
      */
    override def y: Int = {
      _line
    }

    /**
      * @inheritdoc
      */
    override def x: Int = {
      _column
    }

    /**
      * @inheritdoc
      */
    @throws(classOf[NoSuchElementException])
    override def next: Char = {
      val c: Char = iterator.next

      if (c == System.lineSeparator().charAt(0)) {
        _line += 1
        _column = 1
      } else {
        _column += 1
      }

      c
    }

    /**
      * @inheritdoc
      */
    override def hasNext: Boolean = iterator.hasNext

    /**
      * @inheritdoc
      */
    override def lookAhead(n: Int): Array[Char] = {
      val (i1, i2): (Iterator[Char], Iterator[Char]) = iterator.duplicate
      iterator = i1
      i2.slice(0, n).toArray
    }
  }

}
