name := "tadp-2015c2-clase14-implicits-typeclasses"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
   "org.specs2" %% "specs2-core" % "3.6.5" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")