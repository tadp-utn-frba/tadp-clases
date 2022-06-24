package org.uqbar.tadep.macros.enums

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Sin Magia
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

package sinMagia:

  sealed trait Acabado:
    def ordinal: Int

  case object Brillante extends Acabado:
    val ordinal: Int = 0
    
  case object SemiMate extends Acabado:
    val ordinal: Int = 1

  case object Mate extends Acabado:
    val ordinal: Int = 2

  object Acabado:
    def values = Array(Brillante, SemiMate, Mate)

    def valueOf(nombre: String) = nombre match
      case "Brillante" => Brillante
      case "SemiMate" => SemiMate
      case "Mate" => Mate

    def fromOrdinal(ordinal: Int) = ordinal match
      case 0 => Brillante
      case 1 => SemiMate
      case 2 => Mate

//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
// Con Magia
//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

package conMagia:

  enum Acabado:
    case Brillante
    case SemiMate
    case Mate


  enum Color(val codigo: Int):
    case Rojo  extends Color(0xFF0000)
    case Verde extends Color(0x00FF00)
    case Azul  extends Color(0x0000FF)
    case Custom(rgb: Int, override val alpha: Int) extends Color(rgb)

    def alpha: Int = 1
    def esPrimario = codigo % 0xFF == 0
