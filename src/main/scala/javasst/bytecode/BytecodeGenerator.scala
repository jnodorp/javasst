package javasst.bytecode

import javasst.JavaSstType
import javasst.JavaSstType.JavaSstType
import javasst.ast.JavaSstNode
import javasst.ast.JavaSstNodeType._
import javasst.parser.JavaSstParserObject

import cafebabe.AbstractByteCodes._
import cafebabe.ByteCodes.{RETURN => _, _}
import cafebabe.ClassFileTypes._
import cafebabe.Flags._
import cafebabe._
import com.typesafe.scalalogging.Logger
import compiler.ast.Ast
import compiler.parser.SymbolTable
import org.slf4j.LoggerFactory

import scala.language.implicitConversions

/**
  * This class generates bytecode out of an [[compiler.ast.Ast]].
  *
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class BytecodeGenerator(val filename: String, val ast: Ast[JavaSstNode], val className: String) {

  /**
    * The class file.
    */
  private val classFile: ClassFile = new ClassFile(className)

  /**
    * The logger.
    */
  private val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
    * The current code handler.
    */
  private var codeHandler: CodeHandler = null

  /**
    * The default constructor.
    */
  private var constructor: CodeHandler = null

  /**
    * The current methods parameters.
    */
  private var parameters: Seq[JavaSstParserObject] = null

  /**
    * The current methods parameters.
    */
  private var variables: Seq[String] = null

  /**
    * The else label.
    */
  private var elseLabel: String = null

  /**
    * The current symbol table.
    */
  private var symbolTable: SymbolTable[JavaSstParserObject, JavaSstType] = null

  /**
    * The end label.
    */
  private var endLabel: String = null

  /**
    * Create a [[BytecodeGenerator]] without a class name.
    *
    * @param filename The filename.
    * @param ast      The [[compiler.ast.Ast]].
    */
  def this(filename: String, ast: Ast[JavaSstNode]) {
    this(filename, ast, ast.root.obj.identifier)

    classFile.setSourceFile(filename)
    classFile.setFlags(0x00)
    val methodHandler: MethodHandler = classFile.addConstructor(Nil)
    methodHandler.setFlags(0x00)
    constructor = methodHandler.codeHandler << ArgLoad(0) << InvokeSpecial("java/lang/Object", "<init>", "()V")
    ast.beforeLeft = beforeLeftHook
  }

  private def beforeLeftHook(node: JavaSstNode): Unit = {
    node.clazz match {

      // Load object reference.
      case CALL => codeHandler << ArgLoad(0)

      // Add constants.
      case CONSTANT_DECLARATION =>
        classFile.addField(node.obj.typ, node.obj.identifier).setFlags(FIELD_ACC_FINAL)
        constructor << ArgLoad(0) << Ldc(node.constant.get) << PutField(className, node.obj.identifier, node.typ)

      case FIELD => codeHandler << ArgLoad(0)

      // Add class variables.
      case FIELD_DECLARATION =>
        classFile.addField(node.typ, node.obj.identifier).setFlags(0x00)

      // Add code handlers.
      case FUNCTION =>
        val parameters: List[String] = node.obj.parameters.map(x => type2string(x.typ)).toList
        val methodHandler: MethodHandler = classFile.addMethod(node.typ, node.obj.identifier, parameters)
        methodHandler.setFlags(METHOD_ACC_PUBLIC)

        // Update current context.
        codeHandler = methodHandler.codeHandler
        symbolTable = node.obj.symbolTable
        this.parameters = node.obj.parameters
        variables = node.obj.variableDeclarations().map(x => x.identifier)

      // case WHILE => codeHandler << Label(elseLabel)

      // Handle 'do' label for 'while' loops.
      case EQUALS_EQUALS | GREATER_THAN | GREATER_THAN_EQUALS | LESS_THAN | LESS_THAN_EQUALS =>
        if (node.parent.get.clazz == WHILE) codeHandler << Label(elseLabel)

      // Skip all the other cases. We will handle them during the second pass.
      case _ =>
    }
  }

  /**
    * Create bytecode from [[compiler.ast.Ast]].
    *
    * @return The class file.
    */
  def generate(): ClassFile = {
    ast.foreach(node => {
      val obj: JavaSstParserObject = node.obj
      node.clazz match {

        case ASSIGNMENT =>
          node.left.get.clazz match {
            case FIELD => codeHandler << PutField(className, node.left.get.obj.identifier, node.right.get.typ)
            case PARAMETER => codeHandler << IStore(parameters.indexOf(node.left.get.obj) + 1)
            case VARIABLE => codeHandler << IStore(variables.indexOf(node.left.get.obj.identifier) + 1); codeHandler.getFreshVar
            case _ => logger.error(s"Cannot reassign ${node.left.get.clazz}.")
          }

        // Handle method calls.
        case CALL =>
          val signature: String = s"(${obj.parameters.map(x => type2string(x.typ)).mkString("")})${type2string(obj.typ)}"
          codeHandler << InvokeVirtual(className, obj.identifier, signature)

        // Ignore class nodes since we already added them in the first pass.
        case CLASS =>
          constructor << ByteCodes.RETURN
          constructor.print
          constructor.freeze

        // Load constants.
        case CONSTANT => codeHandler << Ldc(node.constant.get)

        // Ignore declarations during second run.
        case CONSTANT_DECLARATION =>

        // Handle '=='.
        case EQUALS_EQUALS =>
          if (node.parent.get.clazz == IF) {
            elseLabel = codeHandler.getFreshLabel("ELSE")
            codeHandler << If_ICmpNe(elseLabel)
          } else if (node.parent.get.clazz == WHILE) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << If_ICmpNe(endLabel)
          } else {
            val els: String = codeHandler.getFreshLabel("EXP_ELSE")
            val end: String = codeHandler.getFreshLabel("EXP_END")
            codeHandler << If_ICmpNe(els) << ICONST_0 << Goto(end) << Label(els) << ICONST_1

            if (node.parent.get.link.isDefined) codeHandler << Label(end)
          }

        // Load field if it is not the left hand side of an assignment.
        case FIELD =>
          if (!(node.parent.get.left.get == node && node.parent.get.clazz == ASSIGNMENT))
            codeHandler << GetField(className, obj.identifier, obj.typ)

        // Ignore declarations during second run.
        case FIELD_DECLARATION =>

        // Freeze the current code handler and set the next.
        case FUNCTION =>
          if (VOID == node.typ) codeHandler << ByteCodes.RETURN
          codeHandler.print
          println("===")

          try {
            codeHandler.freeze
          } catch {
            case c: CodeFreezingException => logger.error(s"Error in method ${obj.identifier}.", c)
            case r: RuntimeException => logger.error(s"Error in method ${obj.identifier}.", r)
          }

        // Handle '>'.
        case GREATER_THAN =>
          if (node.parent.get.clazz == IF) {
            elseLabel = codeHandler.getFreshLabel("ELSE")
            codeHandler << If_ICmpLe(elseLabel)
          } else if (node.parent.get.clazz == WHILE) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << If_ICmpLe(endLabel)
          } else {
            val els: String = codeHandler.getFreshLabel("EXP_ELSE")
            val end: String = codeHandler.getFreshLabel("EXP_END")
            codeHandler << If_ICmpLe(els) << ICONST_0 << Goto(end) << Label(els) << ICONST_1

            if (node.parent.get.link.isDefined) codeHandler << Label(end)
          }

        // Handle '>='.
        case GREATER_THAN_EQUALS =>
          if (node.parent.get.clazz == IF) {
            elseLabel = codeHandler.getFreshLabel("ELSE")
            codeHandler << If_ICmpLt(elseLabel)
          } else if (node.parent.get.clazz == WHILE) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << If_ICmpLt(endLabel)
          } else {
            val els: String = codeHandler.getFreshLabel("EXP_ELSE")
            val end: String = codeHandler.getFreshLabel("EXP_END")
            codeHandler << If_ICmpLt(els) << ICONST_0 << Goto(end) << Label(els) << ICONST_1

            if (node.parent.get.link.isDefined) codeHandler << Label(end)
          }

        // Handle end of 'if'.
        case IF =>
          if (node.parent.get.link.isDefined) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << Goto(endLabel)
          }

          codeHandler << Label(elseLabel)

        // Write 'end' label.
        case IF_ELSE => if (node.link.isDefined) codeHandler << Label(endLabel)

        // Handle '<'.
        case LESS_THAN =>
          if (node.parent.get.clazz == IF) {
            elseLabel = codeHandler.getFreshLabel("ELSE")
            codeHandler << If_ICmpGe(elseLabel)
          } else if (node.parent.get.clazz == WHILE) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << If_ICmpGe(endLabel)
          } else {
            val els: String = codeHandler.getFreshLabel("EXP_ELSE")
            val end: String = codeHandler.getFreshLabel("EXP_END")
            codeHandler << If_ICmpGe(els) << ICONST_0 << Goto(end) << Label(els) << ICONST_1

            if (node.parent.get.link.isDefined) codeHandler << Label(end)
          }

        // Handle '<='.
        case LESS_THAN_EQUALS =>
          if (node.parent.get.clazz == IF) {
            elseLabel = codeHandler.getFreshLabel("ELSE")
            codeHandler << If_ICmpGt(elseLabel)
          } else if (node.parent.get.clazz == WHILE) {
            endLabel = codeHandler.getFreshLabel("END")
            codeHandler << If_ICmpGt(endLabel)
          } else {
            val els: String = codeHandler.getFreshLabel("EXP_ELSE")
            val end: String = codeHandler.getFreshLabel("EXP_END")
            codeHandler << If_ICmpGt(els) << ICONST_0 << Goto(end) << Label(els) << ICONST_1

            if (node.parent.get.link.isDefined) codeHandler << Label(end)
          }

        // Subtract the items on the stack.
        case MINUS => codeHandler << ISUB

        // Load constant.
        case NUMBER => codeHandler << Ldc(node.constant.get)

        // Load parameter.
        case PARAMETER =>
          val parent = node.parent.get
          if (!(parent.left.isDefined && parent.left.get == node && parent.clazz == ASSIGNMENT))
            codeHandler << ArgLoad(parameters.indexOf(obj) + 1)

        // Add two elements from the stack.
        case PLUS => codeHandler << IADD

        // Return the element on the stack.
        case RETURN =>
          node.typ match {
            case INTEGER => codeHandler << IRETURN
            case VOID => codeHandler << ByteCodes.RETURN
            case _ => throw new UnknownError(s"Unknown return type ${node.typ}")
          }

        // Divide the elements on the stack.
        case SLASH => codeHandler << IDIV

        // Multiply the items on the stack.
        case TIMES => codeHandler << IMUL

        // Load local variable if it is not the left hand side of an assignment.
        case VARIABLE =>
          if (!(node.parent.get.left.get == node && node.parent.get.clazz == ASSIGNMENT))
            codeHandler << ILoad(variables.indexOf(node.obj.identifier) + 1)

        // Handle end of 'while'.
        case WHILE => codeHandler << Goto(elseLabel) << Label(endLabel)

        case _ => throw new UnknownError(s"Unknown node class ${node.clazz}.")
      }
    })

    classFile
  }

  /**
    * Convert [[JavaSstType]] to string implicitly.
    *
    * @param javaSstType The [[JavaSstType]].
    * @return The string representation.
    */
  private implicit def type2string(javaSstType: JavaSstType): String = {
    javaSstType match {
      case JavaSstType.INTEGER => "I"
      case JavaSstType.VOID => "V"
      case _ => throw new UnknownError(s"Bytecode unknown for $javaSstType.")
    }
  }

  /**
    * Convert [[JavaSstNodeType]] to string implicitly.
    *
    * @param javaSstNodeType The [[JavaSstNodeType]].
    * @return The string representation.
    */
  private implicit def nodeType2string(javaSstNodeType: JavaSstNodeType): String = {
    javaSstNodeType match {
      case INTEGER => "I"
      case VOID => "V"
      case _ => throw new UnknownError(s"Bytecode unknown for $javaSstNodeType.")
    }
  }
}
