package parameters

case class Pokemon(energia: Int, nivel: Int) {
  def entrenar(g: Gimnasio): Pokemon =
    g.entrenar(this)
}

trait Gimnasio {
  def entrenar(p: Pokemon): Pokemon
}

object CrossFit extends Gimnasio {
  def entrenar(p: Pokemon): Pokemon =
    p.copy(p.energia - 100, p.nivel + 20)
}

object Pilates extends Gimnasio {
  def entrenar(p: Pokemon): Pokemon =
    p.copy(p.energia - 10, p.nivel + 1)
}
