package typeclasses

import org.specs2.mutable.Specification

class PersistenciaTest  extends Specification {

  "Persistencia" should {

    val goku = DragonBall("Goku", "Sayan")
    val homero = Simpsons("Homero", true)
    val cocomiel = Cocomiel("Cocomiel")

    "puedo persistir a goku en mysql y redis" in {
      Persistencia.saveMySql(goku) mustEqual "dragon_ball (raza,nombre) values (Sayan,Goku)"
      Persistencia.saveRedis(goku) mustEqual "dragonball.Goku=>Sayan"
    }

    "puedo guardar a homero solo en redis" in {
      // Persistencia.saveMySql(homero) mustEqual "" // No compila
      Persistencia.saveRedis(homero) mustEqual "simpsons.Homero=>true"
    }

//    "cocomiel no merece ser persistido y por eso no compila" in {
//      // Persistencia.saveMySql(cocomiel) mustEqual "" // No compila
//      // Persistencia.saveRedis(cocomiel) mustEqual "" // No compila
//    }

  }

}
