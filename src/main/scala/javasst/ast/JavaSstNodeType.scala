package javasst.ast

/**
  * Enumeration of possible types.
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
object JavaSstNodeType extends Enumeration {
  type JavaSstNodeType = Value

  val ASSIGNMENT, CALL, CLASS, CONSTANT, CONSTANT_DECLARATION, EQUALS_EQUALS, FIELD, FIELD_DECLARATION, FUNCTION,
  GREATER_THAN, GREATER_THAN_EQUALS, IF, INTEGER, IF_ELSE, LESS_THAN, LESS_THAN_EQUALS, MINUS, NUMBER, PARAMETER, PLUS,
  RETURN, SLASH, TIMES, VARIABLE, VOID, WHILE = Value
}