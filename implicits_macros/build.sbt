val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "tadp-clases",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= List(
      "org.scalatest" %% "scalatest" % "3.2.9" % "test"
    ),
    scalacOptions += "-Xcheck-macros",
  )
