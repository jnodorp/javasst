package javasst.ast

import javasst.ast.JavaSstNodeType.JavaSstNodeType
import javasst.parser.JavaSstParserObject

import compiler.ast.Node

/**
  * A Java SST [[compiler.ast.Node]].
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class JavaSstNode(clazz: JavaSstNodeType = null, typ: JavaSstNodeType = null)
  extends Node[JavaSstNodeType, JavaSstNodeType](clazz, typ) {
  override type Self = JavaSstNode
  override type Object = JavaSstParserObject
}
