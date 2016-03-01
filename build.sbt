name := "javasst"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  // Logging
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

  // Test
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test"
)

unmanagedSourceDirectories in Compile <<= (scalaSource in Compile) (Seq(_))
unmanagedSourceDirectories in Test <<= (scalaSource in Test) (Seq(_))
unmanagedSourceDirectories in Test <++= (javaSource in Test) (Seq(_))