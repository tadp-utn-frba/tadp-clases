package tadp.ageofempires

import java.util.Date
import java.time.LocalDate

class Misil(val anioFabricacion:Int) extends Atacante {
 
  def potencialOfensivo: Int = {
    (2016 - anioFabricacion) * 10
  }
}