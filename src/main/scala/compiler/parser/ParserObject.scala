package compiler.parser

import compiler.scanner.Token

/**
  * An object placed in a [[SymbolTable]].
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
trait ParserObject {

  /**
    * The implementing classes type.
    */
  type Self <: ParserObject

  /**
    * The implementing classes [[compiler.scanner.Token]].
    */
  type T <: Token[_]

  /**
    * Get the identifier.
    *
    * @return The identifier.
    */
  def identifier: String

  /**
    * Get the parameters.
    *
    * @return The parameters.
    */
  def parameters: Seq[Self]

  /**
    * Get the [[compiler.scanner.Token]].
    *
    * @return The [[compiler.scanner.Token]].
    */
  def token: T
}
