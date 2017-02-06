## JavaSST Compiler
This is a compiler for a Java subset created within a lecture held at the [University of Jena][1]
in winter semester 2015/2016. It is written in [Scala][2].

## Error Codes

| Code | Meaning                               | Resolution                                                         |
|------|---------------------------------------|--------------------------------------------------------------------|
|    0 | The compilation finished successfully |                                                                    |
|    3 | Wrong number of arguments             | Specify exactly one argument: The file you want to compile         |
|    4 | File not found                        | Make sure the file you specified exists. Try to use absolute paths |
|    5 | Unable to write output to filesystem  | Make sure the temp directory is writable for your user             |
|    6 | 'dot' tool not in PATH                | Install [GraphViz][3] and add it to your path                      |

[1]: https://www.uni-jena.de
[2]: https://www.scala-lang.org
[3]: https://www.graphviz.org
