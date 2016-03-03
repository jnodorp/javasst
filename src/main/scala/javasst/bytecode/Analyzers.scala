package javasst.bytecode

import java.util.concurrent.TimeoutException
import javasst.ast.JavaSstNodeType.JavaSstNodeType
import javasst.ast.{JavaSstNode, JavaSstNodeType}

import compiler.ast.Ast

/**
  * This class contains functions for semantic analysis. They should be used by applying them to the abstract syntax
  * tree after an otherwise successful compilation.
  *
  * @example
  * {{{
  * val parser: JavaSstParser = new JavaSstParser(scanner)
  * val ast: Ast[JavaSstNode] = parser.parse
  * ast.foreach(Analyzers.allDeclared)
  * }}}
  * @author Julian Schlichtholz
  * @version 1.0.0
  * @todo Add further analyzers.
  */
object Analyzers {

  /**
    * Check whether all nodes have an object assigned.
    *
    * @param ast The [[compiler.ast.Ast]].
    * @return The error.
    */
  def allDeclared(ast: Ast[JavaSstNode]): Option[String] = {
    ast.foreach(node => {
      try {
        node.obj
      } catch {
        case exception: TimeoutException => return Some(s"Could not resolve object for ${node.clazz}.")
      }
    })

    None
  }

  /**
    * Check for unused variables.
    *
    * @param ast The [[compiler.ast.Ast]].
    * @return The error.
    */
  def unusedVariables(ast: Ast[JavaSstNode]): Option[String] = {
    var usedVariables: Seq[String] = Seq()
    var assignedVariables: Seq[String] = Seq()

    ast.foreach(node => {
      node.clazz match {
        case JavaSstNodeType.FUNCTION =>
          val unused = usedVariables.distinct.diff(assignedVariables.distinct)
          if (unused.nonEmpty)
            return Some(s"Assigned but unused variable(s): ${unused.mkString(", ")}")
          assignedVariables = Seq()
          usedVariables = Seq()

        case JavaSstNodeType.VARIABLE =>
          val parent = node.parent.get
          if (!(parent.left.isDefined && parent.left.get == node && parent.clazz == JavaSstNodeType.ASSIGNMENT)) {
            assignedVariables :+= node.obj.identifier
          } else {
            usedVariables :+= node.obj.identifier
          }

        case _ =>
      }
    })

    None
  }

  /**
    * Check for unassigned variables.
    *
    * @param ast The [[compiler.ast.Ast]].
    * @return The error.
    */
  def unassignedVariables(ast: Ast[JavaSstNode]): Option[String] = {
    var usedVariables: Seq[String] = Seq()
    var assignedVariables: Seq[String] = Seq()

    ast.foreach(node => {
      node.clazz match {
        case JavaSstNodeType.FUNCTION =>
          val unassigned = assignedVariables.distinct.diff(usedVariables.distinct)
          if (unassigned.nonEmpty)
            return Some(s"Used but unassigned variable(s): ${unassigned.mkString(", ")}")
          assignedVariables = Seq()
          usedVariables = Seq()

        case JavaSstNodeType.VARIABLE =>
          val parent = node.parent.get
          if (!(parent.left.isDefined && parent.left.get == node && parent.clazz == JavaSstNodeType.ASSIGNMENT)) {
            assignedVariables :+= node.obj.identifier
          } else {
            usedVariables :+= node.obj.identifier
          }

        case _ =>
      }
    })

    None
  }

  /**
    * Check for return statements (missing or wrong type).
    *
    * @param ast The [[compiler.ast.Ast]].
    * @return The error.
    */
  def returnStatements(ast: Ast[JavaSstNode]): Option[String] = {
    var returns: Seq[JavaSstNodeType] = Seq()

    ast.foreach(node => {
      node.clazz match {
        case JavaSstNodeType.FUNCTION =>
          if (returns.exists(_ != node.typ)) return Some(s"Wrong return type for function ${node.obj.identifier}.")

        case JavaSstNodeType.RETURN => returns :+= node.typ
        case _ =>
      }
    })

    None
  }
}
