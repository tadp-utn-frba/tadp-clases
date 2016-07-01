package typeclasses

object MySqlClient {
  def save(tabla: String, columnas: List[String], valores: List[String]) =
    s"$tabla (${columnas.mkString(",")}) values (${valores.mkString(",")})"
}

object RedisClient {
  def save(key: String, value: String) = s"$key=>$value"
}

trait Personaje {
  def nombre: String
}

case class DragonBall(nombre: String, raza: String) extends Personaje

object DragonBall {

  implicit object mysql extends MySqlPersistible[DragonBall] {
    override def tabla(a: DragonBall) = "dragon_ball"

    override def columnas(a: DragonBall) = List("raza", "nombre")

    override def valores(a: DragonBall) = List(a.raza, a.nombre)
  }

  implicit object redis extends RedisPersistible[DragonBall] {
    override def key(a: DragonBall) = s"dragonball.${a.nombre}"

    override def value(a: DragonBall) = a.raza
  }

}

case class Simpsons(nombre: String, pelado: Boolean) extends Personaje

object Simpsons {

  implicit object redis extends RedisPersistible[Simpsons] {
    override def key(a: Simpsons) = s"simpsons.${a.nombre}"

    override def value(a: Simpsons) = a.pelado.toString
  }

}

case class Cocomiel(nombre: String) extends Personaje

@annotation.implicitNotFound(msg = "No se encuentra un implicit para persistir ${A} en MySql.")
trait MySqlPersistible[A] {
  def tabla(a: A): String

  def columnas(a: A): List[String]

  def valores(a: A): List[String]
}

@annotation.implicitNotFound(msg = "No se encuentra un implicit para persistir ${A} en Redis.")
trait RedisPersistible[A] {
  def key(a: A): String

  def value(a: A): String
}

object Persistencia {

  def saveMySql[A: MySqlPersistible](a: A) = {
    val p = implicitly[MySqlPersistible[A]]
    MySqlClient.save(p.tabla(a), p.columnas(a), p.valores(a))
  }

  def saveRedis[A: RedisPersistible](a: A) = {
    val p = implicitly[RedisPersistible[A]]
    RedisClient.save(p.key(a), p.value(a))
  }

}
