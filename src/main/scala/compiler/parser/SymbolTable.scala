package compiler.parser

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
  * A token table.
  *
  * @param enclose The enclosing token table.
  * @tparam O The objects contained within this symbol table.
  * @tparam P The parameter types.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
final class SymbolTable[O <: ParserObject, P <: Enumeration#Value](val enclose: Option[SymbolTable[O, P]] = Option.empty) {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * The default category.
    */
  private val DEFAULT_CATEGORY: String = "default"

  /**
    * The requested [[ParserObject]]s by name, parameters and category.
    */
  private var promised: Map[MultiKey, Promise[O]] = Map()

  /**
    * The [[ParserObject]]s by name, parameters and category.
    */
  private var objects: Map[MultiKey, O] = Map()

  /**
    * Get all [[ParserObject]]s, with a given category, within THIS symbol table.
    *
    * @param category The category.
    * @return All [[ParserObject]]s, with a given category, within THIS symbol table.
    */
  def objects(category: String = DEFAULT_CATEGORY): Seq[O] = {
    objects.filter(pair => pair._1.category == category).values.toSeq
  }

  /**
    * Add a [[ParserObject]].
    *
    * @param obj        The [[ParserObject]].
    * @param parameters The parameters.
    * @param category   The category.
    */
  @throws(classOf[SymbolAlreadyExists])
  def add(obj: O, parameters: Seq[P] = Seq(), category: String = DEFAULT_CATEGORY): Future[O] = {
    val key: MultiKey = new MultiKey(obj.identifier, parameters, category)

    if (objects.get(key).isEmpty) {
      objects += (key -> obj)

      val option: Option[Promise[O]] = resolvePromised(obj.identifier, parameters, category)
      if (option.isDefined) {
        option.get.success(obj)
        logger.debug(s"Resolved $key.")
      }

      Future {
        obj
      }
    } else {
      throw new SymbolAlreadyExists("Tried to add symbol " + obj + " but already defined as " + objects.get(key).get)
    }
  }

  /**
    * Get a future [[ParserObject]] from this or any enclosing symbol table.
    *
    * Usage:
    * <code>Await.result(symbolTable.resolve("x"), Duration(10, TimeUnit.SECONDS))</code>
    *
    * @param name       The objects name.
    * @param parameters The parameter list.
    * @param category   The category.
    * @return The future [[ParserObject]] from this or any enclosing symbol table with the default category.
    */
  def resolve(name: String, parameters: Seq[P] = Seq(), category: String = DEFAULT_CATEGORY): Future[O] = {
    val key: MultiKey = new MultiKey(name, parameters, category)

    if (objects.contains(key)) {
      Future {
        objects.get(key).get
      }
    } else if (enclose.isDefined) {
      enclose.get.resolve(name, parameters, category)
    } else if (promised.contains(key)) {
      promised.get(key).get.future
    } else {
      val promise: Promise[O] = Promise()
      promised += (key -> promise)
      promise.future
    }
  }

  /**
    * Resolve a promise.
    *
    * @param name       The name.
    * @param parameters The parameters.
    * @param category   The category.
    * @return The promise or an empty option if there is no such promise.
    */
  private def resolvePromised(name: String, parameters: Seq[P] = Seq(), category: String = DEFAULT_CATEGORY): Option[Promise[O]] = {
    val multiKey: MultiKey = new MultiKey(name, parameters, category)
    if (promised.contains(multiKey)) {
      val result: Option[Promise[O]] = promised.get(multiKey)
      promised -= multiKey
      result
    } else if (enclose.isDefined) {
      enclose.get.resolvePromised(name, parameters, category)
    } else {
      Option.empty
    }
  }

  /**
    * A key to resolve symbol table entries.
    *
    * @param name       The name.
    * @param parameters The parameters.
    * @param category   The category.
    */
  private class MultiKey(val name: String, val parameters: Seq[P], val category: String) {

    override def equals(o: Any): Boolean = {
      o.isInstanceOf[MultiKey] &&
        o.asInstanceOf[MultiKey].category == category &&
        o.asInstanceOf[MultiKey].name == name &&
        o.asInstanceOf[MultiKey].parameters == parameters
    }

    override def hashCode(): Int = {
      toString.hashCode()
    }

    override def toString: String = {
      "{ \"name\": \"" + name + "\", \"parameters\": " + parameters.mkString("[\"", "\", \"", "\"]") + ", \"category\": \"" + category + "\" }"
    }
  }

}

/**
  * Exception thrown when double defining symbols.
  *
  * @param msg The message.
  */
class SymbolAlreadyExists(val msg: String) extends RuntimeException(msg) {}
