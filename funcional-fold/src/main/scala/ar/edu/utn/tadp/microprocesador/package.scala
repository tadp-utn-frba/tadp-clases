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

	def ejecutar(micro: Microprocesador, programa: Instruccion*): ResultadoDeEjecucion = programa.foldLeft(Ejecutando(micro): ResultadoDeEjecucion){ (resultadoAnterior, instruccionActual) =>
		instruccionActual match {

		case HALT => resultadoAnterior match {
				case Ejecutando(micro) => Halt(micro)
				case otro => otro
			}

			case IFNZ(instruccionesInternas @ _*) => resultadoAnterior match {
				case Ejecutando(micro) =>
					if (micro.a != 0)
						ejecutar(micro pc_+= instruccionActual.bytes, instruccionesInternas: _*) match {
							case Ejecutando(micro) => Ejecutando(micro pc_+= END.bytes)
							case otro => otro
						}
					else Ejecutando(micro pc_+= instruccionActual.bytes + instruccionesInternas.map(_.bytes).sum + END.bytes)
				case otro => otro
			}

			case otra =>
				resultadoAnterior match {
					case Ejecutando(micro) =>
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
					case otro => otro
				}
		}
	}
}
