name := "Microprocesador Objetos-Funcional IV"

description := "2. Ejercicio de Microprocesador aplicando operaciones pseudo-monadicas"

scalaVersion := "2.11.4"

///////////////////////////////////////////////////////////////////////////////////////////////////

lazy val cacao = FDProject(
	"org.scalatest" %% "scalatest" % "2.2.1" % "test",
	"com.novocode" % "junit-interface" % "0.11" % "test"
)


///////////////////////////////////////////////////////////////////////////////////////////////////

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)

unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)

scalacOptions += "-feature"
