package javasst.parser

import javasst.JavaSstType
import javasst.JavaSstType._
import javasst.ast.{JavaSstNode, JavaSstNodeType}
import javasst.scanner.JavaSstToken

import com.typesafe.scalalogging.Logger
import compiler.ast.Ast
import compiler.parser.{Parser, SymbolAlreadyExists, SymbolTable}
import compiler.scanner.Scanner
import org.slf4j.LoggerFactory

/**
  * A parser for Java SST.
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class JavaSstParser(override val scanner: Scanner[JavaSstToken, JavaSstType])
  extends Parser[JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode](scanner) {

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * The function category.
    */
  private val CATEGORY_FUNCTION = "function"

  override protected var current: JavaSstToken = new JavaSstToken("", EOF, 0, 0, "")

  /**
    * The [[compiler.ast.Ast]]
    */
  var ast: Ast[JavaSstNode] = null

  /**
    * Class: {{{class}}} [[JavaSstType.IDENT]]
    *
    * @return The class node.
    */
  def clazz(): JavaSstNode = {
    val n: JavaSstNode = new JavaSstNode(JavaSstNodeType.CLASS)
    var o: JavaSstParserObject = null

    ast = new Ast[JavaSstNode](n)
    scope(() => {
      token(CLASS).once
      val t: JavaSstToken = token(IDENT).once
      o = new JavaSstParserObject(t, CLASS, symbolTable)

      classBody()
    })

    // Add object to symbol table.
    symbolTable.add(o)

    // Finish AST.
    n.obj = o
    n
  }

  /**
    * Class body: &#123; {{{constant()}}} {{{variableDeclaration()}}} {{{functionDeclaration()}}} &#125;.
    */
  def classBody() {
    token(CURLY_BRACE_OPEN).once
    token(FINAL).repeat(() => {
      var parent: JavaSstNode = ast.root
      if (parent.left.isDefined) {
        parent = parent.left.get
        while (parent.link.isDefined) {
          parent = parent.link.get
        }
        parent.link = constant()
      } else {
        parent.left = constant()
      }
    })
    token(first("field_declaration")).repeat(() => {
      var parent: JavaSstNode = ast.root
      if (parent.left.isDefined) {
        parent = parent.left.get
        while (parent.link.isDefined) {
          parent = parent.link.get
        }
        parent.link = fieldDeclaration()
      } else {
        parent.left = fieldDeclaration()
      }
    })
    token(first("function_declaration")).repeat(() => {
      var parent: JavaSstNode = ast.root
      if (parent.right.isDefined) {
        parent = parent.right.get
        while (parent.link.isDefined) {
          parent = parent.link.get
        }
        parent.link = functionDeclaration()
      } else {
        parent.right = functionDeclaration()
      }
    })

    if (CURLY_BRACE_CLOSE != current.typ) {
      error(CURLY_BRACE_OPEN)
    }
  }

  /**
    * Function declaration: {{{public}}} ... [[JavaSstType.IDENT]] ...
    * <p>
    * TODO: Finish documentation.
    * TODO: Add handling for non integer function declarations.
    */
  def functionDeclaration(): JavaSstNode = {
    // Build AST.
    val n: JavaSstNode = new JavaSstNode(JavaSstNodeType.FUNCTION)

    // Verify syntax.
    token(PUBLIC).once
    val t: JavaSstToken = current
    val typ: String = token(Seq(VOID, INTEGER)).once.identifier

    val t1: JavaSstToken = token(IDENT).once
    var st: SymbolTable[JavaSstParserObject, JavaSstType] = null
    scope(() => {
      st = symbolTable
      val parameters: Seq[JavaSstParserObject] = formalParameters()
      parameters.foreach(o => {
        try {
          symbolTable.add(o)
          logger.debug("Adding symbol " + o)
        } catch {
          case e: SymbolAlreadyExists =>
            logger.error("Parameter name already used.", e)
            System.exit(e.hashCode())
        }
      })

      token(CURLY_BRACE_OPEN).once
      token(first("variable_declaration")).repeat(variableDeclaration)

      n.right = statementSequence()
      token(CURLY_BRACE_CLOSE).once
    })

    // Build symbol table.
    val p: JavaSstParserObject = new JavaSstParserObject(t1, FUNCTION, st)
    p.typ = INTEGER

    typ match {
      case "void" =>
        p.typ = VOID
        n.typ = JavaSstNodeType.VOID
      case "int" =>
        p.typ = INTEGER
        n.typ = JavaSstNodeType.INTEGER
      case _ =>
        logger.error("Unknown return type " + t.toString())
        System.exit(-1)
    }

    n.obj = p
    try {
      symbolTable.add(p, p.parameters.map(p => p.typ), CATEGORY_FUNCTION)
    } catch {
      case e: SymbolAlreadyExists =>
        logger.error("Function name already used.", e)
        System.exit(e.hashCode())
    }

    n
  }

  override def parse: Ast[JavaSstNode] = {
    next()
    new Ast[JavaSstNode](clazz())
  }

  /**
    * Constant: {{{final}}} [[typ()]] [[IDENT]] {{{=}}} [[NUMBER]] {{{;}}}.
    * <p>
    * TODO: Add handling for non integer constants.
    * TODO: Add handling for calculated constants.
    */
  private def constant(): JavaSstNode = {
    // Verify syntax.
    token(FINAL).once
    typ()
    val t: JavaSstToken = token(IDENT).once
    token(EQUALS).once
    val integerValue: Int = token(NUMBER).once.identifier.toInt
    token(SEMICOLON).once

    // Create the object.
    val o: JavaSstParserObject = new JavaSstParserObject(t, CONSTANT)
    o.intValue = integerValue
    o.typ = INTEGER
    symbolTable.add(o)

    // Create the node.
    val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.CONSTANT_DECLARATION, JavaSstNodeType.INTEGER)
    node.obj = o
    node.constant = Option(integerValue)
    node
  }

  /**
    * Field declaration: [[typ()]] [[IDENT]] {{{;}}}.
    * <p>
    * TODO: Add handling for non integer field declarations.
    */
  private def fieldDeclaration(): JavaSstNode = {
    // Verify syntax.
    typ()
    val t = token(IDENT).once
    token(SEMICOLON).once

    // Build symbol table entry.
    val o: JavaSstParserObject = new JavaSstParserObject(t, FIELD)
    o.typ = INTEGER
    symbolTable.add(o)

    val node = new JavaSstNode(JavaSstNodeType.FIELD_DECLARATION, JavaSstNodeType.INTEGER)
    node.obj = o
    node
  }

  /**
    * Variable declaration: [[typ()]] [[IDENT]] {{{;}}}.
    * <p>
    * TODO: Add handling for non integer variable declarations.
    */
  private def variableDeclaration(): Unit = {
    // Verify syntax.
    typ()
    val t = token(IDENT).once
    token(SEMICOLON).once

    // Build symbol table entry.
    val o: JavaSstParserObject = new JavaSstParserObject(t, VARIABLE)
    o.typ = INTEGER
    symbolTable.add(o)
  }

  /**
    * TODO: Add handling for non integer parameters.
    */
  private def formalParameters(): Seq[JavaSstParserObject] = {
    token(PARENTHESIS_OPEN).once

    var parameters: Seq[JavaSstParserObject] = Seq()
    token(first("fp_section")).optional(x => {
      typ()
      val t: JavaSstToken = token(IDENT).once

      // Build symbol table.
      val parameter: JavaSstParserObject = new JavaSstParserObject(t, PARAMETER)
      parameter.typ = INTEGER
      parameters :+= parameter

      token(COMMA).repeat(() => {
        next()
        typ()
        val t: JavaSstToken = token(IDENT).once

        // Build symbol table.
        val parameter: JavaSstParserObject = new JavaSstParserObject(t, PARAMETER)
        parameter.typ = INTEGER
        parameters :+= parameter
      })
    })

    token(PARENTHESIS_CLOSE).once

    parameters
  }

  private def statementSequence(): JavaSstNode = {
    val node: JavaSstNode = statement()
    var nodes: Seq[JavaSstNode] = Seq()
    token(first("statement")).repeat(() => nodes :+= statement())

    // Chain statement nodes.
    var current: JavaSstNode = node
    for (n <- nodes) {
      current.link = n
      current = n
    }

    node
  }

  /**
    * TODO: Handle non integer assignments.
    *
    * @return TODO: Documentation.
    */
  private def statement(): JavaSstNode = {
    val t: JavaSstToken = current

    // Could be an assignment or a function call.
    if (IDENT == current.typ) {
      next()

      if (PARENTHESIS_OPEN == current.typ) {
        val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.CALL)

        node.left = actualParameters()

        var parameters: Seq[JavaSstType] = Seq()
        if (node.left.isDefined) {
          var n = node.left.get
          parameters :+= INTEGER
          while (n.link.isDefined) {
            n = n.link.get
            parameters :+= INTEGER
          }
        }

        node.future = symbolTable.resolve(t.identifier, parameters, CATEGORY_FUNCTION)
        token(SEMICOLON).once
        return node
      } else if (EQUALS == current.typ) {
        token(EQUALS).once
        val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.ASSIGNMENT, JavaSstNodeType.INTEGER)

        val left: JavaSstNode = new JavaSstNode(null, JavaSstNodeType.INTEGER)
        left.future = symbolTable.resolve(t.identifier)
        left.obj.objectClass match {
          case CONSTANT => error(Seq(FIELD, PARAMETER, VARIABLE))
          case FIELD => left.clazz = JavaSstNodeType.FIELD
          case PARAMETER => left.clazz = JavaSstNodeType.PARAMETER
          case VARIABLE => left.clazz = JavaSstNodeType.VARIABLE
          case _ => logger.warn("Should not happen...")
        }

        node.left = left
        node.right = expression()
        token(SEMICOLON).once
        return node
      } else {
        error(Seq(PARENTHESIS_OPEN, EQUALS))
      }
    } else if (first("if_statement").contains(current.typ)) {
      return ifStatement()
    } else if (first("while_statement").contains(current.typ)) {
      return whileStatement()
    } else if (first("return_statement").contains(current.typ)) {
      return returnStatement()
    } else {
      error(Seq(IDENT, IF, WHILE, RETURN))
    }

    null
  }

  private def typ(): Unit = {
    token(INTEGER).once
  }

  private def ifStatement(): JavaSstNode = {
    token(IF).once
    val ifElse: JavaSstNode = new JavaSstNode(JavaSstNodeType.IF_ELSE)
    val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.IF)
    ifElse.left = node

    token(PARENTHESIS_OPEN).once
    node.left = expression()
    token(PARENTHESIS_CLOSE).once
    token(CURLY_BRACE_OPEN).once
    node.right = statementSequence()
    token(CURLY_BRACE_CLOSE).once
    token(ELSE).once
    token(CURLY_BRACE_OPEN).once
    ifElse.right = statementSequence()
    token(CURLY_BRACE_CLOSE).once

    ifElse
  }

  private def whileStatement(): JavaSstNode = {
    token(WHILE).once
    val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.WHILE)

    token(PARENTHESIS_OPEN).once
    node.left = expression()
    token(PARENTHESIS_CLOSE).once

    token(CURLY_BRACE_OPEN).once
    node.right = statementSequence()
    token(CURLY_BRACE_CLOSE).once

    node
  }

  /**
    * TODO: Handle non integer and void return types.
    *
    * @return TODO: Documentation.
    */
  private def returnStatement(): JavaSstNode = {
    token(RETURN).once
    val node: JavaSstNode = new JavaSstNode(JavaSstNodeType.RETURN, JavaSstNodeType.VOID)

    token(first("simple_expression")).optional(x => {
      node.left = simpleExpression()
      node.typ = JavaSstNodeType.INTEGER
    })
    token(SEMICOLON).once

    node
  }

  private def actualParameters(): JavaSstNode = {
    val node: JavaSstNode = new JavaSstNode()
    token(PARENTHESIS_OPEN).once
    token(first("simple_expression")).optional(x => {
      node.link = simpleExpression()
      token(COMMA).repeat(() => {
        token(COMMA).once
        var current: JavaSstNode = node
        while (current.link.isDefined) {
          current = current.link.get
        }
        current.link = simpleExpression()
      })
    })

    token(PARENTHESIS_CLOSE).once
    if (node.link.isDefined) {
      node.link.get
    } else {
      null
    }
  }

  private def expression(): JavaSstNode = {
    val node: JavaSstNode = simpleExpression()
    val parent: JavaSstNode = new JavaSstNode()
    token(Seq(EQUALS_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS)).optional(token => {
      next()
      parent.clazz = token.typ match {
        case EQUALS_EQUALS => JavaSstNodeType.EQUALS_EQUALS
        case GREATER_THAN => JavaSstNodeType.GREATER_THAN
        case GREATER_THAN_EQUALS => JavaSstNodeType.GREATER_THAN_EQUALS
        case LESS_THAN => JavaSstNodeType.LESS_THAN
        case LESS_THAN_EQUALS => JavaSstNodeType.LESS_THAN_EQUALS
      }
      parent.right = simpleExpression()
    })

    if (parent.clazz != null) {
      parent.left = node
      return parent
    }

    node
  }

  /**
    * TODO: Handle concatenated expressions.
    *
    * @return TODO: Documentation.
    */
  private def simpleExpression(): JavaSstNode = {
    var node: JavaSstNode = term()
    token(Seq(PLUS, MINUS)).repeat(() => {
      val nodeClazz = current.typ match {
        case PLUS => JavaSstNodeType.PLUS
        case MINUS => JavaSstNodeType.MINUS
      }

      next()
      val old: JavaSstNode = node
      node = new JavaSstNode(nodeClazz, JavaSstNodeType.INTEGER)
      node.left = old
      node.right = term()
    })

    node
  }

  /**
    * TODO: Handle concatenated terms.
    *
    * @return TODO: Documentation.
    */
  private def term(): JavaSstNode = {
    var node: JavaSstNode = factor()
    token(Seq(TIMES, SLASH)).repeat(() => {
      val nodeClazz = current.typ match {
        case TIMES => JavaSstNodeType.TIMES
        case SLASH => JavaSstNodeType.SLASH
      }

      next()
      val old: JavaSstNode = node
      node = new JavaSstNode(nodeClazz, JavaSstNodeType.INTEGER)
      node.left = old
      node.right = factor()
    })

    node
  }

  /**
    * TODO: Handle non integer numbers, variables, calls and constants.
    *
    * @return TODO: Documentation.
    */
  private def factor(): JavaSstNode = {
    var node: JavaSstNode = null
    current.typ match {
      case IDENT =>
        val identifier: String = current.identifier
        next()

        // Could be an internal function call.
        if (PARENTHESIS_OPEN == current.typ) {
          node = new JavaSstNode(JavaSstNodeType.CALL, JavaSstNodeType.INTEGER)
          node.left = actualParameters()

          // Get function call parameters.
          var parameters: Seq[JavaSstType] = Seq()
          if (node.left.isDefined) {
            var n = node.left.get
            parameters :+= INTEGER
            while (n.link.isDefined) {
              n = n.link.get
              parameters :+= INTEGER
            }
          }

          node.future = symbolTable.resolve(identifier, parameters, CATEGORY_FUNCTION)
        } else {
          node = new JavaSstNode(null, JavaSstNodeType.INTEGER)
          node.future = symbolTable.resolve(identifier)
          node.obj.objectClass match {
            case CONSTANT =>
              node.clazz = JavaSstNodeType.CONSTANT
              node.constant = Some(node.obj.intValue)
            case FIELD => node.clazz = JavaSstNodeType.FIELD
            case PARAMETER => node.clazz = JavaSstNodeType.PARAMETER
            case VARIABLE => node.clazz = JavaSstNodeType.VARIABLE
            case _ => logger.warn("Should not happen...")
          }
        }
      case NUMBER =>
        node = new JavaSstNode(JavaSstNodeType.NUMBER, JavaSstNodeType.INTEGER)
        node.constant = Option(current.identifier.toInt)
        next()
      case PARENTHESIS_OPEN =>
        next()
        node = simpleExpression()
        token(PARENTHESIS_CLOSE).once
      case _ =>
        error(Seq(IDENT, NUMBER, PARENTHESIS_OPEN));
    }

    node
  }

  /**
    * Get all possible first [[JavaSstType]] of the construct c.
    *
    * @param c The construct.
    * @return The possible first token types of c.
    */
  private def first(c: String): Seq[JavaSstType] = {
    c match {
      case "type" => Seq(INTEGER)
      case "function_declaration" => first("function_head")
      case "function_head" => Seq(PUBLIC)
      case "fp_section" => first("type")
      case "variable_declaration" => first("type")
      case "field_declaration" => first("type")
      case "statement" => first("assignment") ++ first("function_call") ++ first("if_statement") ++ first("while_statement") ++ first("return_statement")
      case "assignment" => Seq(IDENT)
      case "function_call" => Seq(IDENT)
      case "if_statement" => Seq(IF)
      case "while_statement" => Seq(WHILE)
      case "return_statement" => Seq(RETURN)
      case "simple_expression" => first("term")
      case "term" => first("factor")
      case "factor" => Seq(IDENT, NUMBER, PARENTHESIS_OPEN)
      case "expression" => first("simple_expression")
      case _ => throw new IllegalArgumentException("Invalid construct: '" + c + "'");
    }
  }
}
