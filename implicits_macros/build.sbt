name := "TADP-2016c1-implicits-and-macros"

description := "Ejemplos de Implicits, typeclasses y macros"

scalaVersion := "2.11.6"

///////////////////////////////////////////////////////////////////////////////////////////////////

lazy val cacao = FDProject(
	"org.scalatest" %% "scalatest" % "2.2.1" % "test",
	"com.novocode" % "junit-interface" % "0.11" % "test"
)


///////////////////////////////////////////////////////////////////////////////////////////////////

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)

unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)

scalacOptions += "-feature"
