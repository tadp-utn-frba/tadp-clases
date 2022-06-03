name := "Microprocesador Objetos-Funcional III"

description := "3. Ejercicio de Microprocesador reemplazando la recursividad por una reducción"

scalaVersion := "2.11.12"

///////////////////////////////////////////////////////////////////////////////////////////////////

lazy val cacao = FDProject(
	"org.scalatest" %% "scalatest" % "2.2.1" % "test",
	"com.novocode" % "junit-interface" % "0.11" % "test"
)


///////////////////////////////////////////////////////////////////////////////////////////////////

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)

unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)

scalacOptions += "-feature"
