package ar.edu.utn.tadp

import scala.language.implicitConversions
import scala.util.Try
import scala.util.Success

package object microprocesador {

  trait ResultadoDeEjecucion {
    def micro: Microprocesador
    def map(f: (Microprocesador => Microprocesador)): ResultadoDeEjecucion
    def filter(f: (Microprocesador => Boolean)): ResultadoDeEjecucion
    def flatMap(f: (Microprocesador => ResultadoDeEjecucion)): ResultadoDeEjecucion
    def fold[T](e: (Microprocesador => T))(f: (Microprocesador => T)): T
  }

  case class Ejecutando(val micro: Microprocesador) extends ResultadoDeEjecucion {
    def map(f: (Microprocesador => Microprocesador)) = Ejecutando(f(micro))
    def filter(f: (Microprocesador => Boolean)) = if (f(micro)) this else Error(micro, "Fallo el filtrado")
    def flatMap(f: (Microprocesador => ResultadoDeEjecucion)) = f(micro)
    def fold[T](e: (Microprocesador => T))(f: (Microprocesador => T)): T = f(micro)
  }

  case class Halt(val micro: Microprocesador) extends ResultadoDeEjecucion {
    def map(f: (Microprocesador => Microprocesador)) = this
    def filter(f: (Microprocesador => Boolean)) = this
    def flatMap(f: (Microprocesador => ResultadoDeEjecucion)) = this
    def fold[T](e: (Microprocesador => T))(f: (Microprocesador => T)): T = e(micro)
  }

  case class Error(val micro: Microprocesador, descripcion: String) extends ResultadoDeEjecucion {
    def map(f: (Microprocesador => Microprocesador)) = this
    def filter(f: (Microprocesador => Boolean)) = this
    def flatMap(f: (Microprocesador => ResultadoDeEjecucion)) = this
    def fold[T](e: (Microprocesador => T))(f: (Microprocesador => T)): T = e(micro)
  }

  // ****************************************************************
  // ** ORDEN SUPERIOR
  // ****************************************************************

  def ejecutar(micro: Microprocesador, programa: Instruccion*): ResultadoDeEjecucion = programa.foldLeft(Ejecutando(micro): ResultadoDeEjecucion) { (resultadoAnterior, instruccionActual) =>

    val resultado = for (micro <- resultadoAnterior)
      yield micro pc_+= instruccionActual.bytes

    instruccionActual match {

      case HALT => resultado.fold(_ => resultado) { micro => Halt(micro) }

      case IFNZ(instruccionesInternas @ _*) =>

        resultado.flatMap { micro =>
          if (micro.a != 0) ejecutar(micro, instruccionesInternas: _*).map { _ pc_+= END.bytes }
          else Ejecutando(micro pc_+= instruccionesInternas.map(_.bytes).sum + END.bytes)
        }

      /*case DIV => resultado.flatMap 
        { micro => if(micro.b == 0) 
                    Error(micro, "Division por cero.")
                   else 
                    Ejecutando(micro.guardar(micro.a/micro.b))}
        */

      case DIV => resultado.filter { micro => micro.b != 0 }.
        map { micro => micro.guardar(micro.a / micro.b) }

      case otra =>
        resultadoAnterior.map { micro =>
          val siguienteMicro = otra match {
            case NOP            => micro
            case ADD            => micro.guardar(micro.a + micro.b)
            case MUL            => micro.guardar(micro.a * micro.b)
            case SWAP           => micro.copy(a = micro.b, b = micro.a)
            case LODV(valor)    => micro.copy(a = valor)
            case LOD(direccion) => micro.copy(a = micro.memoriaDeDatos(direccion))
            case STR(direccion) => micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(direccion, micro.a))
          }
          siguienteMicro.pc_+=(otra.bytes)
        }

    }
  }

  // ****************************************************************
  // ** FOR COMPREHENSION
  // ****************************************************************

  //		def ejecutar(micro: Microprocesador, programa: Instruccion*): ResultadoDeEjecucion = programa.foldLeft(Ejecutando(micro): ResultadoDeEjecucion){ (resultadoAnterior, instruccionActual) =>
  //	
  //			val resultado = for (micro <- resultadoAnterior) yield micro pc_+= instruccionActual.bytes
  //			//		val resultado = resultadoAnterior.map { micro => micro pc_+= instruccionActual.bytes }
  //	
  //			instruccionActual match {
  //				case HALT => resultado.fold(_ => resultado){ micro => Halt(micro) }
  //	
  //				case ifInstruction @ IFNZ(instruccionesInternas @ _*) => 
  //          for {
  //					micro <- resultado
  //					microPostInternas <- ejecutar(micro, 
  //              instruccionesInternas: _*)
  //				} yield if (micro.a == 0)
  //					micro pc_+= instruccionesInternas.map(_.bytes).sum + END.bytes
  //				  else microPostInternas pc_+= END.bytes
  //				//				resultado.flatMap{micro =>
  //				//					ejecutar(micro,instruccionesInternas: _*).map { microPostInternas =>
  //				//						if (micro.a == 0) micro pc_+= instruccionesInternas.map(_.bytes).sum + END.bytes else microPostInternas pc_+= END.bytes
  //				//					}
  //				//				}
  //	
  //        case DIV => for {
  //          micro <- resultado
  //          if micro.b != 0
  //        } yield micro.guardar(micro.a/micro.b)
  //        
  //				case instruccion => for (micro <- resultado) 
  //          yield instruccion match {
  //					case NOP => micro
  //					case ADD => micro.guardar(micro.a + micro.b)
  //					case MUL => micro.guardar(micro.a * micro.b)
  //					case SWAP => micro.copy(a = micro.b, b = micro.a)
  //					case LODV(valor) => micro.copy(a = valor)
  //					case LOD(direccion) => micro.copy(a = micro.memoriaDeDatos(direccion))
  //					case STR(direccion) => micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(direccion, micro.a))
  //				}
  //			}
  //		}

  // ****************************************************************
  // ** USANDO TRY
  // ****************************************************************

//  case class ProgramHalted(micro: Microprocesador) extends Exception
//
//  def ejecutar(micro: Microprocesador, programa: Instruccion*): Try[Microprocesador] =
//    programa.foldLeft(Try(micro)) {
//      (resultadoAnterior, instruccionActual) =>
//
//        val resultado = for (micro <- resultadoAnterior)
//          yield micro pc_+= instruccionActual.bytes
//
//        instruccionActual match {
//          case HALT =>
//            resultado.transform(micro =>
//              Try(throw ProgramHalted(micro)),
//              _ => resultado)
//
//          case ifInstruction @ IFNZ(instruccionesInternas @ _*) =>
//            for {
//              micro <- resultado
//              microPostInternas <- ejecutar(micro, instruccionesInternas: _*)
//            } yield if (micro.a == 0) micro pc_+= ifInstruction.innerBytes + END.bytes else microPostInternas pc_+= END.bytes
//
//          case instruccion => for (micro <- resultado) yield instruccion match {
//            case NOP            => micro
//            case ADD            => micro.guardar(micro.a + micro.b)
//            case MUL            => micro.guardar(micro.a * micro.b)
//            case SWAP           => micro.copy(a = micro.b, b = micro.a)
//            case LODV(valor)    => micro.copy(a = valor)
//            case LOD(direccion) => micro.copy(a = micro.memoriaDeDatos(direccion))
//            case STR(direccion) => micro.copy(memoriaDeDatos = micro.memoriaDeDatos.updated(direccion, micro.a))
//          }
//        }
//    }

}