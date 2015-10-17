package ar.edu.utn.tadp

import scala.language.implicitConversions

package object microprocesador {
  implicit def SeqToList[T](s: Seq[T]) = s.toList

  trait ResultadoDeEjecucion
  case class Ejecutando(micro: Microprocesador) extends ResultadoDeEjecucion
  case class Halt(micro: Microprocesador) extends ResultadoDeEjecucion
  case class Error(micro: Microprocesador, descripcion: String) extends ResultadoDeEjecucion

  // ****************************************************************
  // ** EJECUTAR
  // ****************************************************************

  def ejecutar(micro: Microprocesador, programa: Instruccion*): ResultadoDeEjecucion = programa.toList match {
    case Nil => Ejecutando(micro)
    case instruccionActual :: restantes =>
      val resultado = instruccionActual match {
        case HALT => Halt(micro)
        case IFNZ(instruccionesInternas @ _*) =>
          if (micro.a != 0)
            ejecutar(micro pc_+= instruccionActual.bytes,
              instruccionesInternas: _*) match {
                case Ejecutando(m) => Ejecutando(m pc_+= END.bytes)
                case otro => otro
              }
          else Ejecutando(micro pc_+= instruccionActual.bytes +
            instruccionesInternas.map(_.bytes).sum + END.bytes)
        
        case DIV => 
          if(micro.b == 0)
            Error(micro, "Division por 0.")
          Ejecutando(micro.guardar(micro.a/micro.b))
            
        case otra =>
          val siguienteMicro = otra match {
            case NOP => micro
            case ADD => micro.guardar(micro.a + micro.b)
            case MUL => micro.guardar(micro.a * micro.b)
            case SWAP => micro.copy(a = micro.b, b = micro.a)
            case LODV(valor) => micro.copy(a = valor)
            case LOD(direccion) => micro.copy(a = micro.memoriaDeDatos(direccion))
            case STR(direccion) => micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(direccion, micro.a))
          }
          Ejecutando(siguienteMicro.pc_+=(otra.bytes))
      }

      resultado match {
        case Ejecutando(micro) => ejecutar(micro, restantes: _*)
        case x => x
      }
      
  }

  // ****************************************************************
  // ** SIMPLIFICAR
  // ****************************************************************

  def simplificar(programa: Instruccion*): Seq[Instruccion] = programa.toList match {
    case Nil => Nil
    case NOP :: restantes => simplificar(restantes: _*)
    case SWAP :: SWAP :: restantes => simplificar(restantes: _*)
    case LODV(_) :: LODV(y) :: restantes => simplificar(LODV(y) :: restantes: _*)
    case LOD(_) :: LOD(y) :: restantes => simplificar(LOD(y) :: restantes: _*)
    case STR(_) :: STR(y) :: restantes => simplificar(STR(y) :: restantes: _*)
    case IFNZ() :: restantes => simplificar(restantes: _*)
    case IFNZ(instrucciones @ _*) :: restantes =>
      val simplificacion = simplificar(instrucciones: _*)
      if (simplificacion.isEmpty) simplificar(restantes: _*)
      else IFNZ(simplificacion: _*) :: simplificar(restantes: _*)
    case siguiente :: restantes => siguiente :: simplificar(restantes: _*)
  }

}
