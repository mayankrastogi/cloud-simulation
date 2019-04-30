name := "Mayank_K_Rastogi_project"

version := "0.1"

scalaVersion := "2.12.8"

// Merge strategy to avoid deduplicate errors
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
  // Typesafe Configuration Library
  "com.typesafe" % "config" % "1.3.2",

  // Logback logging framework
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.gnieh" % "logback-config" % "0.3.1",

  // CloudSim Plus
  "org.cloudsimplus" % "cloudsim-plus" % "4.3.2",

  // Scalatest testing framework
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
)
