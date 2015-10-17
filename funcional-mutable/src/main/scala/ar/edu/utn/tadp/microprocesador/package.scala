package ar.edu.utn.tadp

import scala.language.implicitConversions

package object microprocesador {

  implicit def SeqToList[T](s: Seq[T]) = s.toList

  // ****************************************************************
  // ** EJECUTAR
  // ****************************************************************

  class HaltException extends Exception

  def ejecutar(micro: Microprocesador, programa: Instruccion*) = {
    def ejecutarR(programa: List[Instruccion]): Unit = 
      programa match {
      case Nil =>
      case HALT :: _ => throw new HaltException
      case siguiente :: restantes =>
      	siguiente match {
          case NOP =>
          case ADD => micro.guardar(micro.a + micro.b)
          case MUL => micro.guardar(micro.a * micro.b)
          case SWAP =>
            val temp = micro.a
            micro.a = micro.b
            micro.b = temp
          case LODV(valor) => micro.a = valor
          case LOD(direccion) => micro.a = micro.memoriaDeDatos(direccion)
          case STR(direccion) => micro.memoriaDeDatos(direccion) = micro.a
          case IFNZ(instrucciones @ _*) =>
            micro.pc += siguiente.bytes
            if (micro.a != 0) ejecutarR(instrucciones)
            else micro.pc += instrucciones.map(_.bytes).sum
        }

        micro.pc += siguiente.bytes
        ejecutarR(restantes)
    }

    try ejecutarR(programa.toList)
    catch {
      case e: HaltException =>
    }
  }

  // ****************************************************************
  // ** SIMPLIFICAR
  // ****************************************************************

  def simplificar(programa: Instruccion*): Seq[Instruccion] = {
	  	programa.toList match {
	  	  case Nil => Nil
	  	  case NOP :: restantes => simplificar(restantes: _*)
	  	  case SWAP :: SWAP :: restantes => simplificar(restantes: _*)   
	  	  case LODV(_) :: LODV(y) :: restantes => 
	  	    simplificar ( LODV(y) :: restantes: _*) 
	  	  case LOD(_) :: LOD(y) :: restantes => 
	  	    simplificar ( LOD(y) :: restantes: _*)
	  	  case STR(_) :: STR(y) :: restantes => 
	  	    simplificar ( STR(y) :: restantes: _*)
	  	  case IFNZ() :: restantes => simplificar(restantes: _*)
	  	  case IFNZ(instrucciones @ _*) :: restantes => 
	  	      val r = simplificar(instrucciones: _*) 
	  	      if(r.isEmpty) simplificar(restantes: _*)
	  	      else IFNZ(r: _*) :: simplificar( restantes: _*) 
	  	  case siguiente :: restantes => 
	  	    siguiente :: simplificar(restantes : _*)
	  	  
	  	}
  }

}
