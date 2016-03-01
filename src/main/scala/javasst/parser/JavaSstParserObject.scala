package javasst.parser

import javasst.JavaSstType
import javasst.JavaSstType.JavaSstType
import javasst.scanner.JavaSstToken

import compiler.parser.{ParserObject, SymbolTable}

/**
  * Create a new [[JavaSstParserObject]] with a [[compiler.parser.SymbolTable]].
  *
  * @param token       The [[javasst.scanner.JavaSstToken]].
  * @param objectClass The [[JavaSstType]].
  * @param symbolTable The [[compiler.parser.SymbolTable]].
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class JavaSstParserObject(var token: JavaSstToken, val objectClass: JavaSstType, val symbolTable: SymbolTable[JavaSstParserObject, JavaSstType] = null)
  extends ParserObject {
  override type Self = JavaSstParserObject
  override type T = JavaSstToken

  /**
    * Get the type.
    *
    * @return The type.
    */
  private var typInternal: JavaSstType = null

  /**
    * The int value.
    */
  private var intValueInternal: Int = 0

  /**
    * Get the int value.
    *
    * @return The int value.
    */
  def intValue: Int = intValueInternal

  /**
    * Set the int value.
    *
    * @param arg The int value.
    */
  def intValue_=(arg: Int): Unit = intValueInternal = arg

  /**
    * Get the function declarations.
    *
    * @return The function declarations.
    */
  def functionDeclarations(): Seq[JavaSstParserObject] = {
    symbolTable.objects("function").filter(o => JavaSstType.FUNCTION == o.objectClass).toList
  }

  /**
    * Get the variable declarations.
    *
    * @return The variable declarations.
    */
  def variableDeclarations(): Seq[JavaSstParserObject] = {
    symbolTable.objects().filter(o => JavaSstType.VARIABLE == o.objectClass || JavaSstType.FIELD == o.objectClass).toList
  }

  override def toString: String = {
    token.toString
  }

  override def equals(any: Any): Boolean = {
    if (!any.isInstanceOf[JavaSstParserObject]) {
      return false
    }

    val obj: JavaSstParserObject = any.asInstanceOf[JavaSstParserObject]
    if (!obj.identifier.equals(identifier)) {
      return false
    }

    if (obj.parameters.size != parameters.size) {
      return false
    }

    for (i <- obj.parameters.indices) {
      if (parameters(i).typ != obj.parameters(i).typ) {
        return false
      }
    }

    true
  }

  /**
    * Get the type.
    *
    * @return The type.
    */
  def typ: JavaSstType = typInternal

  /**
    * Set the type.
    *
    * @param arg The type.
    */
  def typ_=(arg: JavaSstType): Unit = typInternal = arg

  override def identifier: String = {
    token.identifier
  }

  override def parameters: Seq[JavaSstParserObject] = {
    if (symbolTable != null) {
      symbolTable.objects().filter(o => JavaSstType.PARAMETER == o.objectClass)
    } else {
      Seq()
    }
  }
}