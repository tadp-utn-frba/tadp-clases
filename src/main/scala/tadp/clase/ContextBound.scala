package tadp.clase

class Importanteable[A] {
  def importante(x: A) = "ok"
}

object ContextBound {

  def necesitoImportante[A: Importanteable](x: A) =
    implicitly[Importanteable[A]].importante(x)

}
