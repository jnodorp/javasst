package javasst.parser

import java.util.concurrent.TimeUnit
import javasst.JavaSstType
import javasst.JavaSstType.JavaSstType
import javasst.ast.JavaSstNode
import javasst.scanner.{JavaSstScanner, JavaSstToken}

import compiler.ast.Ast
import org.scalatest.FlatSpec
import compiler.parser.{Parser, SymbolTable}
import compiler.scanner.Scanner

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Test class for [[JavaSstParser]].
  */
class JavaSstParserSpec extends FlatSpec {

  behavior of "Parser"

  it should "parse the file test.sst correctly" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(getClass.getResource("/javasst/parser/test.sst").getFile)
    val parser: Parser[JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode] = new JavaSstParser(scanner)

    parser.parse
  }

  it should "throw an exception when parsing the error_test.sst" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(getClass.getResource("/javasst/parser/error_test.sst").getFile)
    val parser: Parser[JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode] = new JavaSstParser(scanner)

    assertThrows[RuntimeException](parser.parse)
  }

  it should "produce a correct symbol table" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(getClass.getResource("/javasst/parser/table_test.sst").getFile)
    val parser: JavaSstParser = new JavaSstParser(scanner)

    parser.parse

    var root: SymbolTable[JavaSstParserObject, JavaSstType] = parser.symbolTable
    while (root.enclose.isDefined) {
      root = root.enclose.get
    }

    assert(root.enclose.isEmpty)

    val a: JavaSstParserObject = Await.result(root.resolve("A"), Duration(10, TimeUnit.SECONDS))
    assert(1 == a.functionDeclarations().size)
    assert("f" == a.functionDeclarations().head.identifier)
    assert(JavaSstType.CLASS == a.objectClass)
    assert(a.symbolTable != null)
    assert(1 == a.variableDeclarations().size)
    assert("y" == a.variableDeclarations().head.identifier)

    val b: JavaSstParserObject = Await.result(a.symbolTable.resolve("b"), Duration(10, TimeUnit.SECONDS))
    assert("b" == b.identifier)
    assert(3 == b.intValue)
    assert(JavaSstType.CONSTANT == b.objectClass)
    assert(JavaSstType.INTEGER == b.typ)

    val f: JavaSstParserObject = Await.result(a.symbolTable.resolve("f", Seq(JavaSstType.INTEGER), "function"), Duration(10, TimeUnit.SECONDS))
    assert("f" == f.identifier)
    assert(1 == f.parameters.size)
    assert("x" == f.parameters.head.identifier)
    assert(f.symbolTable != null)

    val x: JavaSstParserObject = Await.result(f.symbolTable.resolve("x"), Duration(10, TimeUnit.SECONDS))
    assert("x" == x.identifier)
    assert(JavaSstType.INTEGER == x.typ)
    assert(f.symbolTable != null)
  }

  it should "parse the file expression_test.sst correctly" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(getClass.getResource("/javasst/parser/expression_test.sst").getFile)
    val parser: Parser[JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode] = new JavaSstParser(scanner)
    val ast: Ast[JavaSstNode] = parser.parse

    for (i <- 1 to 8) {
      assert(ast.toString.contains("<constant> " + i), s"Missing constant: '$i'.")
    }
  }
}