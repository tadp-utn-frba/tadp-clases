package parameters

import org.scalatest.{Matchers, WordSpec}

class GimnasioTest extends WordSpec with Matchers {

  "Gimnasio" should {

    "entrenar sin implicit" in {
      val gimnasio = CrossFit
      val pikachu = Pokemon(500, 1)
      val ratata = Pokemon(200, 1)

      pikachu.entrenar(gimnasio) shouldEqual Pokemon(400, 21)
      ratata.entrenar(gimnasio) shouldEqual Pokemon(100, 21)
    }

//    "entrenar en gimnasio default" in {
//      implicit val gimnasio = CrossFit
//      val pikachu = Pokemon(500, 1)
//      val ratata = Pokemon(200, 1)
//
//      pikachu.entrenar shouldEqual Pokemon(400, 21)
//      ratata.entrenar shouldEqual Pokemon(100, 21)
//    }

    // el gimnasio puede venir por imports
    // companion object
    // herencia

  }

}
