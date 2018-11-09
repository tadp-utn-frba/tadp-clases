package typeclasses

import org.scalatest.{Matchers, WordSpec}

class OrderingTest extends WordSpec with Matchers {

  "Ordering" should {

    "lista ordenada" in {

      case class ListaOrdenada[A <: Ordered[A]](as: A*) {

        def insertar(a: A): ListaOrdenada[A] = {
          // la implementacion ordenaría usando ´compare´
          as.map(_.compare(a))
          this
        }

      }

      case class User(id: Long, name: String) extends Ordered[User] {
        override def compare(that: User): Int = id.compare(that.id)
      }

      ListaOrdenada(User(1, "Pepe"))
        .insertar(User(2, "Jose"))
    }

    "lista ordenable" in {

      case class Lista[A](as: A*) {

        def ordernar(orden: Ordering[A]): Lista[A] = {
          // la implementacion ordenaría usando ´compare´ entre pares de elementos
          orden.compare(as(0), as(0))
          this
        }

      }

      Lista(1).ordernar(Ordering.Int)
    }

    "sort users using Ordered" in {
      case class User(id: Long) extends Ordered[User] {
        override def compare(that: User): Int = id.compare(that.id)
      }

      val result = List(User(2), User(1)).sorted
      result shouldEqual List(User(1), User(2))

      List(3, 1, 2).sorted shouldEqual List(1, 2, 3)
    }

    "sort users using Ordering" in {
      case class User(id: Long, name: String)

      // Orden de usuarios por ID
      val userIdOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int =
          x.id.compare(y.id)
      }

      // Orden de usuarios por Nombre
      val userNameOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int =
          x.name.compare(y.name)
      }

      // ordeno por id
      List(
        User(2, "Jose"),
        User(1, "Pepe")
      ).sorted(userIdOrder) shouldEqual List(
        User(1, "Pepe"),
        User(2, "Jose")
      )

      // ordeno por nombre
      List(
        User(1, "Pepe"),
        User(2, "Jose")
      ).sorted(userNameOrder) shouldEqual List(
        User(2, "Jose"),
        User(1, "Pepe")
      )

      // puedo definir el orden por default usando implicit parameters
      implicit val default = userIdOrder

      List(
        User(2, "Jose"),
        User(1, "Pepe")
      ).sorted(userIdOrder) shouldEqual List(
        User(1, "Pepe"),
        User(2, "Jose")
      )
    }

    "sort composed Ordering" in {

      case class User(id: Long, name: String)

      case class Message[T](content: T)

      // orden de usuarios por Id
      implicit val userIdOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int =
          x.id.compare(y.id)
      }

      // Orden de mensajes por contenido
      implicit def messageOrdering[T](implicit subOrd: Ordering[T]) =
        new Ordering[Message[T]] {

          override def compare(x: Message[T], y: Message[T]): Int = {
            subOrd.compare(x.content, y.content)
            // también puedo usar compare de forma infija
            // usando el conversion import Ordered.orderingToOrdered
          }

        }

      // ordeno mensajes de usuarios
      List(
        Message(User(2, "Jose")),
        Message(User(1, "Pepe"))
      ).sorted shouldEqual List(
        Message(User(1, "Pepe")),
        Message(User(2, "Jose"))
      )

      // también puedo ordenar mensajes de Ints
      List(
        Message(2),
        Message(3),
        Message(1)
      ).sorted shouldEqual List(
        Message(1),
        Message(2),
        Message(3)
      )
    }

  }

}
