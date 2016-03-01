package javasst.scanner

import java.io.FileNotFoundException
import javasst.JavaSstType.{JavaSstType, _}

import org.scalatest.FlatSpec
import compiler.scanner.Scanner

import scala.util.matching.Regex

/**
  * Test class for [[JavaSstScanner]].
  */
class JavaSstScannerSpec extends FlatSpec {

  behavior of "Scanner"

  it should "perform look ahead without side effects" in {
    val file: String = getClass.getResource("/javasst/parser/test.sst").getPath
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(file)

    assert('/' == scanner.input.next)

    val res = scanner.lookahead("/*", COMMENT_START, COMMENT_STOP, new Regex("\\*"))
    assert(res == COMMENT_START)

    assert('*' == scanner.input.next)
  }

  it should "scan the test.sst file correctly" in {
    val file: String = getClass.getResource("/javasst/parser/test.sst").getPath
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(file)

    // class class NiceClassName6 {
    var symbol: JavaSstToken = scanner.next()
    assert(CLASS == symbol.typ)
    assert("class" == symbol.identifier)
    assert(file == symbol.source)
    assert(4 == symbol.line)
    assert(1 == symbol.column)

    symbol = scanner.next()
    assert(IDENT == symbol.typ)
    assert("className6" == symbol.identifier)
    assert(file == symbol.source)
    assert(4 == symbol.line)
    assert(7 == symbol.column)

    symbol = scanner.next()
    assert(CURLY_BRACE_OPEN == symbol.typ)
    assert("{" == symbol.identifier)
    assert(file == symbol.source)
    assert(4 == symbol.line)
    assert(18 == symbol.column)

    // final int const1 = 123;
    symbol = scanner.next()
    assert(FINAL == symbol.typ)
    assert("final" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(5 == symbol.column)

    symbol = scanner.next()
    assert(INTEGER == symbol.typ)
    assert("int" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(11 == symbol.column)

    symbol = scanner.next()
    assert(IDENT == symbol.typ)
    assert("intConst1" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(15 == symbol.column)

    symbol = scanner.next()
    assert(EQUALS == symbol.typ)
    assert("=" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(25 == symbol.column)

    symbol = scanner.next()
    assert(NUMBER == symbol.typ)
    assert("123" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(27 == symbol.column)

    symbol = scanner.next()
    assert(SEMICOLON == symbol.typ)
    assert(";" == symbol.identifier)
    assert(file == symbol.source)
    assert(5 == symbol.line)
    assert(30 == symbol.column)

    // int var1;
    assert(INTEGER == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(SEMICOLON == scanner.next().typ)

    // public int getVar1() {
    assert(PUBLIC == scanner.next().typ)
    assert(INTEGER == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(CURLY_BRACE_OPEN == scanner.next().typ)

    // return var1;
    assert(RETURN == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(SEMICOLON == scanner.next().typ)

    // }
    assert(CURLY_BRACE_CLOSE == scanner.next().typ)

    // public void setIntVar1(int x) {
    assert(PUBLIC == scanner.next().typ)
    assert(VOID == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(INTEGER == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(CURLY_BRACE_OPEN == scanner.next().typ)

    // var1 = x;
    assert(IDENT == scanner.next().typ)
    assert(EQUALS == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(SEMICOLON == scanner.next().typ)

    // }
    assert(CURLY_BRACE_CLOSE == scanner.next().typ)

    // public void incrementVar1() {
    assert(PUBLIC == scanner.next().typ)
    assert(VOID == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(CURLY_BRACE_OPEN == scanner.next().typ)

    // setIntVar1(add(getIntVar1(), 1))
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(IDENT == scanner.next().typ)
    assert(PARENTHESIS_OPEN == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(COMMA == scanner.next().typ)
    assert(NUMBER == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(PARENTHESIS_CLOSE == scanner.next().typ)
    assert(SEMICOLON == scanner.next().typ)

    // }
    assert(CURLY_BRACE_CLOSE == scanner.next().typ)

    // TODO: Finish test.
  }

  it should "return the next element" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_next.txt").getPath)
    assert('A' == scanner.input.next)
  }

  it should "return the current column" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_pos.txt").getPath)
    assert(scanner.input.x == 1)
    scanner.input.next()
    // assert(scanner.input.input.x == 2)
    scanner.input.next()
    assert(scanner.input.x == 1)
    scanner.input.next()
    assert(scanner.input.x == 2)
    scanner.input.next()
    assert(scanner.input.x == 3)
    scanner.input.next()
    assert(scanner.input.x == 1)
    scanner.input.next()
    assert(scanner.input.x == 2)
    scanner.input.next()
    assert(scanner.input.x == 3)
    scanner.input.next()
    assert(scanner.input.x == 4)
  }

  it should "return the input file" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_next.txt").getPath)
    assert(scanner.source == getClass.getResource("/scanner/input_test_next.txt").getPath)
  }

  it should "return the current line" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_pos.txt").getPath)
    assert(scanner.input.y == 1)
    scanner.input.next()
    assert(scanner.input.y == 1)
    scanner.input.next()
    assert(scanner.input.y == 2)
    scanner.input.next()
    assert(scanner.input.y == 2)
    scanner.input.next()
    assert(scanner.input.y == 2)
    scanner.input.next()
    assert(scanner.input.y == 3)
    scanner.input.next()
    assert(scanner.input.y == 3)
    scanner.input.next()
    assert(scanner.input.y == 3)
    scanner.input.next()
    assert(scanner.input.y == 3)
  }

  it should "return if it has a next element" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_next.txt").getPath)
    assert(scanner.input.hasNext)
    assert(scanner.input.hasNext)
    assert('A' == scanner.input.next())
    assert(!scanner.input.hasNext)
    assert(!scanner.input.hasNext)
  }

  it should "perform a valid lookahead" in {
    val scanner: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test_lookahead.txt").getPath)
    assert('T' == scanner.input.next())
    assert('h' == scanner.input.next())
    assert('i' == scanner.input.next())

    assert(scanner.input.lookAhead(10) sameElements Array('s', ' ', 'i', 's', ' ', 'a', ' ', 'l', 'o', 'n'))
    assert(scanner.input.lookAhead(10) sameElements Array('s', ' ', 'i', 's', ' ', 'a', ' ', 'l', 'o', 'n'))

    assert('s' == scanner.input.next())
    assert(' ' == scanner.input.next())
    assert('i' == scanner.input.next())
  }

  it should "throw a FileNotFoundException when given an invalid file" in {
    assertThrows[FileNotFoundException](new JavaSstScanner("not existing"))
  }

  it should "throw a NoSuchElementException when trying to call next at the end of file" in {
    val input: JavaSstScanner = new JavaSstScanner(getClass.getResource("/scanner/input_test.txt").getPath)
    assertThrows[NoSuchElementException](input.input.next == -1.toChar)
  }
}