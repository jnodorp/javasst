package compiler.scanner

/**
  * Implementations of this trait provide the ability to perform a look ahead on any iterator.
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
trait LookAhead[T] extends BufferedIterator[T] {

  /**
    * Returns the next element of the iterator without advancing beyond it.
    *
    * @return The next element of the iterator.
    */
  override def head: T = lookAhead(1)(0)

  /**
    * Get the next {{{n}}} values as an array. The original iterator stays the same.
    *
    * @param n The number of values to look ahead.
    * @throws NoSuchElementException Thrown if there are not enough elements left.
    * @return An [[Array]] of the next {{{n}}} values.
    */
  @throws(classOf[NoSuchElementException])
  def lookAhead(n: Int): Array[T]
}
