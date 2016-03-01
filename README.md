## JavaSST Compiler
This is a compiler for a Java subset created within a lecture held by [Wolfram Amme][1] at the [University of Jena][2]
in winter semester 2015/2016. It is written in [Scala][3].

## Error Codes

| Code | Meaning                               | Resolution                                                         |
|------|---------------------------------------|--------------------------------------------------------------------|
|    0 | The compilation finished successfully |                                                                    |
|    3 | Wrong number of arguments             | Specify exactly one argument: The file you want to compile         |
|    4 | File not found                        | Make sure the file you specified exists. Try to use absolute paths |
|    5 | Unable to write output to filesystem  | Make sure the temp directory is writable for your user             |
|    6 | 'dot' tool not in PATH                | Install [GraphViz][5] and add it to your path                      |

[1]: http://swt.informatik.uni-jena.de/Mitarbeiter/Amme+Wolfram.html
[2]: https://www.uni-jena.de
[3]: https://www.scala-lang.org
[4]: https://docs.oracle.com/javase/specs/jvms/se8/html/index.html
[5]: https://www.graphviz.org
