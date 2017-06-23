package tadp

import org.scalatest.{Matchers, WordSpec}

class OrderingTest extends WordSpec with Matchers {

  "Ordering" should {

    "sort users using Ordered" in {
      case class User(id: Long, name: String) extends Ordered[User] {
        override def compare(that: User): Int = id.compare(that.id)
      }

      val result = List(User(2, "Jose"), User(1, "Pepe")).sorted
      result shouldEqual List(User(1, "Pepe"), User(2, "Jose"))
    }

    "sort users using Ordering" in {
      case class User(id: Long, name: String)

      implicit val userIdOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int = x.id.compare(y.id)
      }

      val userNameOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int = x.name.compare(y.name)
      }

      val idSorted = List(User(2, "Jose"), User(1, "Pepe")).sorted
      idSorted shouldEqual List(User(1, "Pepe"), User(2, "Jose"))

      val nameSorted = List(User(2, "Jose"), User(1, "Pepe")).sorted(userNameOrder)
      nameSorted shouldEqual List(User(2, "Jose"), User(1, "Pepe"))
    }

    "sort composed Ordering" in {
      case class User(id: Long, name: String)
      case class Message[T](content: T)

      implicit def messageOrder[T: Ordering] = new Ordering[Message[T]] {
        import Ordered._

        override def compare(x: Message[T], y: Message[T]): Int = x.content.compare(y.content)
      }

      implicit val userIdOrder = new Ordering[User] {
        override def compare(x: User, y: User): Int = x.id.compare(y.id)
      }

      val idSorted = List(Message(User(2, "Jose")), Message(User(1, "Pepe"))).sorted
      idSorted shouldEqual List(Message(User(1, "Pepe")), Message(User(2, "Jose")))

      val numMessages = List(Message(2), Message(3), Message(1)).sorted
      numMessages shouldEqual List(Message(1), Message(2), Message(3))
    }

  }

}
