package tadp_reflection_scala

object A {

}

object type_tags_1 {
  def detectorDeEnteros(list: List[Any]) = list match {
    case list: List[Int] => true // WARNING! El [Int] no existe en runtime. Sólo List
    case other           => false
  }


  detectorDeEnteros(List("no", "somos", "enteros"))
}
