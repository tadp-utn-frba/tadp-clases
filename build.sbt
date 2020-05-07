organization := "ar.edu.utn.frba.tadp"

name := "granja"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
    "Typesafe" at "http://repo.typesafe.com",
    "Maven Central Repo" at "http://search.maven.org"
    )

libraryDependencies ++= List(
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
        )