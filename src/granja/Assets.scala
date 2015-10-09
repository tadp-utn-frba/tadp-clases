package granja

import scala.collection.mutable.Set

class Corral(val animales: Set[Animal])

class Pastor {
  def pastorear(animales: Set[Animal]) = animales.foreach (_.come)
}

class Lechero {
  //def ordeñar(corral:Corral) = corral.animales.foreach(_.ordeñate)
}