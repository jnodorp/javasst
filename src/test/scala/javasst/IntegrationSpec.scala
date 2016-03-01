package javasst

import javasst.JavaSstType.JavaSstType
import javasst.scanner.{JavaSstScanner, JavaSstToken}

import compiler.scanner.Scanner
import org.scalatest.FlatSpec

/**
  * Some integration tests.
  */
class IntegrationSpec extends FlatSpec {

  behavior of "the whole system"

  it should "compile class_test.sst successfully" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(
      getClass.getResource("/javasst/integration/class_test.sst").getFile)

    // class
    var token: JavaSstToken = scanner.next()
    assert("class" == token.identifier)
    assert(JavaSstType.CLASS == token.typ)

    // className1
    token = scanner.next()
    assert("className1" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // {
    token = scanner.next()
    assert("{" == token.identifier)
    assert(JavaSstType.CURLY_BRACE_OPEN == token.typ)

    // }
    token = scanner.next()
    assert("}" == token.identifier)
    assert(JavaSstType.CURLY_BRACE_CLOSE == token.typ)
  }

  it should "compile classbody_test.sst successfully" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(
      getClass.getResource("/javasst/integration/classbody_test.sst").getFile)

    // class
    var token: JavaSstToken = scanner.next()
    assert("class" == token.identifier)
    assert(JavaSstType.CLASS == token.typ)

    // className1
    token = scanner.next()
    assert("className1" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // {
    token = scanner.next()
    assert("{" == token.identifier)
    assert(JavaSstType.CURLY_BRACE_OPEN == token.typ)

    // final
    token = scanner.next()
    assert("final" == token.identifier)
    assert(JavaSstType.FINAL == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intConst1
    token = scanner.next()
    assert("intConst1" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // =
    token = scanner.next()
    assert("=" == token.identifier)
    assert(JavaSstType.EQUALS == token.typ)

    // 123
    token = scanner.next()
    assert("123" == token.identifier)
    assert(JavaSstType.NUMBER == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // final
    token = scanner.next()
    assert("final" == token.identifier)
    assert(JavaSstType.FINAL == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intConst2
    token = scanner.next()
    assert("intConst2" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // =
    token = scanner.next()
    assert("=" == token.identifier)
    assert(JavaSstType.EQUALS == token.typ)

    // 456
    token = scanner.next()
    assert("456" == token.identifier)
    assert(JavaSstType.NUMBER == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // final
    token = scanner.next()
    assert("final" == token.identifier)
    assert(JavaSstType.FINAL == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intConst3
    token = scanner.next()
    assert("intConst3" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // =
    token = scanner.next()
    assert("=" == token.identifier)
    assert(JavaSstType.EQUALS == token.typ)

    // 789
    token = scanner.next()
    assert("789" == token.identifier)
    assert(JavaSstType.NUMBER == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intVar1
    token = scanner.next()
    assert("intVar1" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intVar2
    token = scanner.next()
    assert("intVar2" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // int
    token = scanner.next()
    assert("int" == token.identifier)
    assert(JavaSstType.INTEGER == token.typ)

    // intVar3
    token = scanner.next()
    assert("intVar3" == token.identifier)
    assert(JavaSstType.IDENT == token.typ)

    // ;
    token = scanner.next()
    assert(";" == token.identifier)
    assert(JavaSstType.SEMICOLON == token.typ)

    // }
    token = scanner.next()
    assert("}" == token.identifier)
    assert(JavaSstType.CURLY_BRACE_CLOSE == token.typ)
  }

  it should "compile function_declaration_test.sst successfully" in {
    val scanner: Scanner[JavaSstToken, JavaSstType] = new JavaSstScanner(
      getClass.getResource("/javasst/integration/function_declaration_test.sst").getFile)

    while (scanner.input.hasNext) {
      scanner.next()
    }
  }
}
