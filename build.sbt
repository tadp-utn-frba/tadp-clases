import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ar.edu.frba.utn.tadp"
ThisBuild / organizationName := "UTN"

lazy val root = (project in file("."))
  .settings(
    name := "tadp-clases",
    libraryDependencies += scalaTest % Test
  )

libraryDependencies += "junit" % "junit" % "4.12" % Test
