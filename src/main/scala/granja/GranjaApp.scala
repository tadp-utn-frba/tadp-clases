package granja

import scala.collection.mutable.Set

object GranjaApp extends App {
  var animal: Animal = new Vaca
  var vaca: Vaca = new Vaca // new Animal

  animal.come
  vaca.ordeñate
  
  vaca = new VacaLoca
  
  vaca.ordeñate
  //vaca.reite

  //------------------------------

  var unaColeccion = Set(new Vaca, new Caballo, new Granero)
  //unaColeccion.filter { unElemento => unElemento.estaGordo }

  //var unaColeccion: Set[Animal] = Set(new Vaca, new Caballo, new Granero)

  //-----------------------------

  val corralito = new Corral(Set(new Vaca, new Vaca, new Vaca))
  val lechero = new Lechero
  val pastor = new Pastor

  //val corralRaro = new Corral(Set(1,2,3))

  //pastor.pastorear(corralito.animales)

  //lechero.ordeñar(corralito)

  //------------------------------

  var vacas: Set[Vaca] = Set[Vaca]()
  var animales: Set[Animal] = ??? //Set[Vaca]()

  animales.foreach { animal => animal.come }
  vacas.foreach { vaca => vaca.ordeñate }

  animales.add(new Caballo) // Opa! Un caballo es un animal, así que esto vale
  vacas.foreach { vaca => vaca.ordeñate } //Eh… No.

  // var animales: List[Animal] = List[Vaca]()



  //------------------------------

  //  val c = new Corral[Int](Set(1,2,3))

  //  c.contiene[Int](123)

  //  c.contiene[Vaca](new Vaca)
  //  c.contiene[AnyRef]("una vaca")

}