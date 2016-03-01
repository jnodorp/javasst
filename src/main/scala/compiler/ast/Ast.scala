package compiler.ast

/**
  * The abstract syntax tree (AST). Traversal happens via left recursive descent.
  *
  * @param root The root.
  * @tparam N The ASTs nodes.
  * @author Julian Schlichtholz
  * @version 1.0.0
  */
class Ast[N <: Node[_, _]](val root: N) extends Traversable[N] {

  /**
    * The traverse hook is notified for each [[Node]] visited. This hook is called before traversing the left [[Node]].
    */
  var beforeLeft: (N) => Any = N => ()

  /**
    * The traverse hook is notified for each [[Node]] visited. This hook is called before traversing the right [[Node]].
    */
  var beforeRight: (N) => Any = N => ()

  /**
    * The traverse hook is notified for each [[Node]] visited. This hook is called before traversing the link [[Node]].
    */
  var beforeLink: (N) => Any = N => ()

  /**
    * Convert the [[Ast]] to a string in dot format. Refer to <a href="http://graphviz.org/">http://graphviz.org/</a>
    * for further information on the format and tools.
    */
  override def toString: String = {
    var nodes: Seq[Node[_, _]] = Seq[Node[_, _]]()

    // Prepare string with node definitions.
    val result: StringBuilder = new StringBuilder("digraph AST {").append(System.lineSeparator())
    foreach(node => {
      result.append("\t")
      result.append(node.toDot(nodes.size.toString))
      result.append(System.lineSeparator())
      nodes = nodes :+ node
    })
    result.append(System.lineSeparator())

    // Add edges to string.
    foreach(node => {
      val s: String = "\t\"" + nodes.indexOf(node) + "\" -> \""
      if (node.left.isDefined) {
        result.append(s).append(nodes.indexOf(node.left.get))
        result.append("\" [label=\"left\"]").append(";").append(System.lineSeparator())
      }

      if (node.right.isDefined) {
        result.append(s).append(nodes.indexOf(node.right.get))
        result.append("\" [label=\"right\"]").append(";").append(System.lineSeparator())
      }

      if (node.link.isDefined) {
        result.append(s).append(nodes.indexOf(node.link.get))
        result.append("\" [label=\"link\"]").append(";").append(System.lineSeparator())
      }

      // Uncomment the following lines to display parent edges in output.
      // if (node.parent.isDefined) {
      //   result.append(s).append(nodes.indexOf(node.parent.get))
      //   result.append("\" [label=\"parent\"]").append(";").append(System.lineSeparator())
      // }
    })

    result.append("}").toString()
  }

  /**
    * Traverse the [[Ast]]. This is the same as traversing the [[Ast]] using the given function as the {{{beforeLink}}}
    * hook.
    *
    * @param f The function to apply.
    * @tparam U The functions return type.
    */
  override def foreach[U](f: (N) => U): Unit = {
    val backup: (N) => Any = beforeLink
    beforeLink = f
    traverse()
    beforeLink = backup
  }

  /**
    * Traverse the [[Ast]] beginning with the given [[Node]].
    *
    * @param startNode The start [[Node]].
    */
  def traverse[U](startNode: N = root) {

    var node: N = startNode
    while (node != null) {
      beforeLeft(node)
      if (node.left.isDefined) {
        traverse(node.left.get.asInstanceOf[N])
      }

      beforeRight(node)
      if (node.right.isDefined) {
        traverse(node.right.get.asInstanceOf[N])
      }

      beforeLink(node)
      if (node.link.isDefined) {
        node = node.link.get.asInstanceOf[N]
      } else {
        node = null.asInstanceOf[N]
      }
    }
  }
}
