package compiler.scanner

/**
  * Implementations of this trait provide some information about their (current) position.
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
trait Position {

  /**
    * The source. This should be the file path if the input is file like and some kind of descriptor otherwise.
    */
  val source: String

  /**
    * Get the current x coordinate (e. g. the column, the horizontal position, etc.).
    *
    * @return The current x coordinate.
    */
  def x: Int

  /**
    * Get the current y coordinate (e. g. the line, the row, the vertical position, etc.).
    *
    * @return The current y coordinate.
    */
  def y: Int
}
