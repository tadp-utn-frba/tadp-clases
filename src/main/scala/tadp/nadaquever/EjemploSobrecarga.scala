package tadp.nadaquever

class Persona 

class Rey extends Persona

class Perro

object Saludador {
  def saludar(a: Persona) = "Holaaaa"
  def saludar(b: Rey) = "Salve o su majestad"
  def saludar(c: Perro) = "Perriiitoooo"
  def saludar(a: Persona, c: Perro) = "Señor, lindo perro"
  
  //Esto no se puede
  //def saludar(a: Persona, c: Perro) = 2
}

object Sobrecarga extends App {
  val a : Persona = new Persona
  val b : Rey = new Rey
  
  val ab : Persona = b
  
  val c : Perro = new Perro
  
  
  Saludador.saludar(a) // "Holaaaa"
  Saludador.saludar(b) // "Salve o su majestad"
  Saludador.saludar(c) // "Perriiitoooo"
  
  Saludador.saludar(ab) // "Holaaaa"
  
  //To be continued: Polimorfismo Paramétrico y Pattern Matching
}