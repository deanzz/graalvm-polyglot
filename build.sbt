name := "graalvm-polyglot"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  //"org.graalvm.truffle" % "truffle-api" % "19.1.1",
  "org.graalvm.sdk" % "graal-sdk" % "19.1.1",
)

mainClass in assembly := Some("graal.polyglot.Main")
assemblyJarName in assembly := "graal-polyglot.jar"
/*
assemblyMergeStrategy in assembly := {
  case PathList("module-info.java") => MergeStrategy.first
  case PathList("module-info.class") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}*/
