package typeclasses

import org.scalatest.{Matchers, WordSpec}

class OrderingTest extends WordSpec with Matchers {

  "Ordering" should {

    "lista ordenada" in {
      case class User(id: Long, name: String) extends Ordered[User] {
        override def compare(that: User): Int = id.compare(that.id)
      }

      List(User(2, "Jose"), User(1, "Pepe")).sorted shouldEqual
        List(User(1, "Pepe"), User(2, "Jose"))
    }

    "puedo comparar ordenables" in {
      // Si quiero usar "Ordenables" puedo restringir el tipo
      def comparar[A <: Ordered[A]](a: A, b: A): Int = a.compare(b)

      case class User(id: Long, name: String) extends Ordered[User] {
        override def compare(that: User): Int = id.compare(that.id)
      }

      comparar(User(2, "Jose"), User(1, "Pepe")) shouldEqual 1
      comparar(User(1, "Pepe"), User(2, "Jose")) shouldEqual -1
    }

    "lista ordenable" in {
      case class User(id: Long, name: String)

      object OrdenPorId extends Ordering[User] {
        override def compare(user1: User, user2: User): Int =
          user1.id.compare(user2.id)
      }

      object OrdenPorNombre extends Ordering[User] {
        override def compare(user1: User, user2: User): Int =
          user1.name.compare(user2.name)
      }

      List(User(2, "Jose"), User(1, "Pepe")).sorted(OrdenPorId) shouldEqual
        List(User(1, "Pepe"), User(2, "Jose"))

      List(User(2, "Jose"), User(1, "Pepe"), User(3, "Alberto")).sorted(OrdenPorNombre) shouldEqual
        List(User(3, "Alberto"), User(2, "Jose"), User(1, "Pepe"))

      // puedo ordenar con un orden default
      // List(User(2, "Jose"), User(1, "Pepe")).sorted shouldEqual
      //   List(User(1, "Pepe"), User(2, "Jose"))
    }

    "puedo combinar Orderings" in {

      case class User(id: Long, name: String)

      case class Message[A](content: A)

      // orden de usuarios por Id
      val userIdOrder =
        new Ordering[User] {
          override def compare(x: User, y: User): Int =
            x.id.compare(y.id)
        }

      def messageOrdering[A](contentOrdering: Ordering[A]) =
        new Ordering[Message[A]] {
          override def compare(x: Message[A], y: Message[A]): Int =
            contentOrdering.compare(x.content, y.content)
        }

      // ordeno mensajes de usuarios
      val messageUserOrd = messageOrdering(userIdOrder)

      List(Message(User(2, "Jose")), Message(User(1, "Pepe"))).sorted(messageUserOrd) shouldEqual
        List(Message(User(1, "Pepe")), Message(User(2, "Jose")))

      // --------------

      val messageIntOrd = messageOrdering(Ordering.Int)
      List(Message(2), Message(3), Message(1)).sorted(messageIntOrd) shouldEqual
        List(Message(1), Message(2), Message(3))
    }

    "sort composed Ordering" in {

      case class User(id: Long, name: String)

      case class Message[A](content: A)

      // orden de usuarios por Id
      implicit val userIdOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int =
          x.id.compare(y.id)
      }

      implicit def messageOrdering[A](implicit contentOrdering: Ordering[A]) =
        new Ordering[Message[A]] {
          override def compare(x: Message[A], y: Message[A]): Int =
            contentOrdering.compare(x.content, y.content)
        }

      // ordeno mensajes de usuarios
      List(Message(User(2, "Jose")), Message(User(1, "Pepe"))).sorted shouldEqual
        List(Message(User(1, "Pepe")), Message(User(2, "Jose")))

      // Las listas de Int son ordenables (viene con scala)
      List(2, 3, 1).sorted shouldEqual
        List(1, 2, 3)

      // Entonces, también puedo ordenar mensajes de Ints
      List(Message(2), Message(3), Message(1)).sorted shouldEqual
        List(Message(1), Message(2), Message(3))
    }

  }

}
