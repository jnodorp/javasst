package compiler.scanner

/**
  * A token is an identifier used within a programming language. E.g. int, class, etc..
  *
  * @param identifier The identifier.
  * @param typ        The type.
  * @param line       The line.
  * @param column     The column.
  * @param source     The source.
  * @tparam E The tokens type enumeration.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
abstract class Token[E <: Enumeration#Value](val identifier: String, val typ: E, val line: Int, val column: Int, val source: String) {

  override def toString: String = {
    "%s %s at position %d:%d in %s".format(typ, identifier, line, column, source)
  }
}
