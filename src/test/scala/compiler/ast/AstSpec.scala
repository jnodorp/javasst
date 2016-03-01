package compiler.ast

import compiler.ast.TestNodeType.{TestNodeType, _}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

import scala.io.Source

/**
  * Test class for [[Ast]].
  */
class AstTest extends FlatSpec with BeforeAndAfterEach {

  /**
    * The file.
    */
  private val file: String = getClass.getResource("/ast/ast.dot").getPath

  /**
    * The [[Ast]].
    */
  protected var ast: Ast[TestNode] = null

  override def beforeEach: Unit = {
    val root: TestNode = new TestNode(ROOT)

    val n0: TestNode = new TestNode(N0)
    val n1: TestNode = new TestNode(N1)
    val n2: TestNode = new TestNode(N2)
    val n3: TestNode = new TestNode(N3)
    val n4: TestNode = new TestNode(N4)
    val n5: TestNode = new TestNode(N5)
    val n6: TestNode = new TestNode(N6)
    val n7: TestNode = new TestNode(N7)
    val n8: TestNode = new TestNode(N8)
    val n9: TestNode = new TestNode(N9)
    val n10: TestNode = new TestNode(N10)
    val n11: TestNode = new TestNode(N11)

    root.left = n0
    root.right = n1
    root.link = n2

    n0.left = n3
    n0.right = n4
    n0.link = n5

    n1.left = n6
    n1.right = n7
    n1.link = n8

    n2.left = n9
    n2.right = n10
    n2.link = n11

    ast = new Ast[TestNode](root)
  }

  behavior of "AST"

  it should "traverse correctly" ignore {
    fail("Not yet implemented.")
  }

  it should "have a unchangeable root node" in {
    val root: TestNode = new TestNode(ROOT)
    val ast: Ast[TestNode] = new Ast[TestNode](root)
    assert(root == ast.root, "The AST should not modify the root node.")
  }

  it should "output a valid dot source on to string" in {
    val dot: String = ast.toString()
    val expected: String = Source.fromFile(file).mkString

    assert(expected == dot, "The ASTs dot output should not change.")
  }
}

class TestNode(clazz: TestNodeType.Value) extends Node[TestNodeType, TestNodeType](clazz, null) {
  override type Self = TestNode
}