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

  }

}
