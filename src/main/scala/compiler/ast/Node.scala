package compiler.ast

import java.util.concurrent.{TimeUnit, TimeoutException}

import compiler.parser.ParserObject

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * A [[Node]] of the abstract syntax tree [[Ast]].
  *
  * @param clazz The class.
  * @param typ   The type.
  * @tparam C The [[Node]]s class enumeration.
  * @tparam T The [[Node]]s type enumeration.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
abstract class Node[C <: Enumeration#Value, T <: Enumeration#Value](var clazz: C = null, var typ: T = null) {

  /**
    * The implementing classes type.
    */
  type Self <: Node[C, T]

  /**
    * The implementations [[compiler.parser.ParserObject]] type.
    */
  type Object <: ParserObject

  /**
    * The constant.
    */
  var constant: Option[Int] = Option.empty

  /**
    * The future [[compiler.parser.ParserObject]]
    */
  var future: Future[Object] = null

  /**
    * The parent [[Node]].
    */
  var parent: Option[Node[C, T]] = Option.empty

  /**
    * The left [[Node]].
    */
  private var _left: Option[Self] = Option.empty

  /**
    * The right [[Node]].
    */
  private var _right: Option[Self] = Option.empty

  /**
    * The link [[Node]].
    */
  private var _link: Option[Self] = Option.empty

  /**
    * The object.
    */
  private var _obj: Option[Object] = Option.empty

  /**
    * Create the dot representation for this Node.
    *
    * @return The dot representation of this Node.
    */
  def toDot(name: String): String = {
    val cl: String = if (clazz == null) "-" else clazz.toString
    val t: String = if (typ == null) "-" else typ.toString
    val o: String = if (obj != null) obj.identifier else "-"
    val co: String = if (constant.isDefined) constant.get.toString else "-"

    "\"" + name + "\" [shape=record, label=\"{" + "<class> " + cl + " | " +
      "<type> " + t + " | " +
      "<object> " + o + " | " +
      "<constant> " + co +
      "}\"];"
  }

  /**
    * Get the [[compiler.parser.ParserObject]].
    *
    * @return The [[compiler.parser.ParserObject]].
    */
  def obj: Object = {
    if (_obj.isDefined) {
      _obj.get
    } else if (future != null) {
      try {
        _obj = Option(Await.result(future, Duration(1000, TimeUnit.MILLISECONDS)))
        _obj.get
      } catch {
        case e: TimeoutException => null.asInstanceOf[Object]
      }
    } else {
      null.asInstanceOf[Object]
    }
  }

  /**
    * Set the [[compiler.parser.ParserObject]].
    *
    * @param parserObject The [[compiler.parser.ParserObject]].
    */
  def obj_=(parserObject: Object): Unit = {
    _obj = Option(parserObject)
  }

  /**
    * Get the left [[Node]].
    *
    * @return The left [[Node]].
    */
  def left: Option[Self] = {
    _left
  }

  /**
    * Set the left [[Node]].
    *
    * @param node The left [[Node]].
    */
  def left_=(node: Self): Unit = {
    if (node != null) node.parent = Option(this)
    _left = Option(node)
  }

  /**
    * Get the right [[Node]].
    *
    * @return The right [[Node]].
    */
  def right: Option[Self] = {
    _right
  }

  /**
    * Set the right [[Node]].
    *
    * @param node The right [[Node]].
    */
  def right_=(node: Self): Unit = {
    node.parent = Option(this.asInstanceOf[Self])
    _right = Option(node)
  }

  /**
    * Get the link [[Node]].
    *
    * @return The link [[Node]].
    */
  def link: Option[Self] = {
    _link
  }

  /**
    * Set the link [[Node]].
    *
    * @param node The link [[Node]].
    */
  def link_=(node: Self): Unit = {
    node.parent = Option(this.asInstanceOf[Self])
    _link = Option(node)
  }
}
