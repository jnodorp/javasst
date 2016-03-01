package javasst.scanner

import javasst.JavaSstType.JavaSstType

import compiler.scanner.Token

/**
  * This class contains constants for all terminal symbols.
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class JavaSstToken(override val identifier: String, override val typ: JavaSstType, override val line: Int,
                   override val column: Int, override val source: String)
  extends Token[JavaSstType](identifier, typ, line, column, source) {
}
