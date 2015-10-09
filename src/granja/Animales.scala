package granja

class Animal {
  var peso = 100

  def come = peso += 10
  def estaGordo = peso >= 150
}

class Vaca extends Animal {
  def ordeñate = peso -= 10
}

class VacaLoca extends Vaca {
  def reite = "Muajajajjaajja"
}

class Caballo extends Animal {
  def relincha = "IEIIEIEeeeeieeie"
}