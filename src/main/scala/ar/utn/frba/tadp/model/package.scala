package ar.utn.frba.tadp

import scala.language.implicitConversions

package object model {
  val squareSeparator = "║"
  val boardLineSeparator = "═════╬═════╬═════"

  sealed trait Player {
    val symbol: SquareValue
    def other: Player

    override def toString: String = symbol.toString
  }
  case class HumanPlayer(symbol: SquareValue) extends Player {
    def other: Player = IAPlayer(symbol.other)
  }
  case class IAPlayer(symbol: SquareValue) extends Player {
    def other: Player = HumanPlayer(symbol.other)
  }

  case class Square(position: Position, value: SquareValue = SquareValue.Empty) {
    def assignTo(player: Player): Square =
      if (isEmpty) copy(value = player.symbol)
      else throw WrongUserInputException("Invalid play: Square already in use")

    def isEmpty: Boolean = value == SquareValue.Empty

    override def toString: String = s"  ${value.toString}  "
  }
  object Square {
    private val squareMap: Map[Int, Position] = Map(
      1 -> Position(0, 0), 2 -> Position(1, 0), 3 -> Position(2, 0),
      4 -> Position(0, 1), 5 -> Position(1, 1), 6 -> Position(2, 1),
      7 -> Position(0, 2), 8 -> Position(1, 2), 9 -> Position(2, 2)
    )

    val emptySquares: List[Square] = squareMap.values.toList.map(Square(_))

    def fromSquareNumber(squareNumber: Int): Position = squareMap.apply(squareNumber)
  }

  case class Position(column: Int, row: Int)

  sealed abstract class SquareValue(val symbol: String) {
    def other: SquareValue

    override def toString: String = symbol
  }
  object SquareValue {
    val playableValues: List[SquareValue] = List(O, X)

    def apply(symbol: String): SquareValue = symbol.toUpperCase match {
      case "X" => X
      case "O" => O
      case _ => throw WrongUserInputException(s"Player must be one of $X or $O")
    }

    case object X extends SquareValue("X") {
      def other: SquareValue = O
    }
    case object O extends SquareValue("O") {
      def other: SquareValue = X
    }
    case object Empty extends SquareValue("-") {
      def other: SquareValue = throw new RuntimeException("should not happen!")
    }
  }

  case class Board(squares: List[Square]) {
    lazy val orderedRows: Map[Int, List[Square]] = rows.view.mapValues(_.sortBy(_.position.column)).toMap

    val rows: Map[Int, List[Square]] = squares.groupBy(_.position.row) //       0 -> (0,0 - 1,0 - 2,0), 1 -> (0,1 - 1,1 - 2,1), 0 -> (0,2 - 1,2 - 2,2)
    val columns: Map[Int, List[Square]] = squares.groupBy(_.position.column) // 0 -> (0,0 - 0,1 - 0,2), 1 -> (1,0 - 1,1 - 1,2), 0 -> (2,0 - 2,1 - 2,2)

    val principalDiagonal: List[Square] = squares.filter(square => square.position.row == square.position.column) // 0,0 - 1,1 - 2,2
    val secondaryDiagonal: List[Square] = List(square(0, 2), square(1, 1), square(2, 0)) // 0,2 - 1,1 - 2,0

    val lines: List[List[Square]] = rows.values.toList ++ columns.values.toList ++ List(principalDiagonal, secondaryDiagonal)

    val status: BoardStatus = if (winner.isDefined || isComplete) BoardStatus.Finished else BoardStatus.OnGoing

    def square(position: Position): Square = squares.find { square => position == square.position }.get

    def emptySquares: List[Square] = squares.filter(_.isEmpty)

    def isComplete: Boolean = emptySquares.isEmpty

    def ongoing: Boolean = status == BoardStatus.OnGoing

    def winner: Option[SquareValue] = SquareValue
      .playableValues
      .find(value => lines.exists(_.map(_.value) == List(value, value, value)))

    def play(position: Position, player: Player): Board = {
      val playedSquare = square(position)
      copy(squares = playedSquare.assignTo(player) :: squares.filterNot(_ == playedSquare))
    }
  }
  object Board {
    val empty: Board = Board(Square.emptySquares)
  }

  sealed trait BoardStatus
  object BoardStatus {
    case object OnGoing extends BoardStatus
    case object Finished extends BoardStatus
  }

  case class Turn(board: Board, currentPlayer: Player, nextPlayer: Player) {
    def nextTurn(nextBoard: Board): Turn = Turn(nextBoard, currentPlayer = nextPlayer, nextPlayer = currentPlayer)
  }

  case class WrongUserInputException(message: String) extends RuntimeException(message)

  implicit def tupleToPosition(tuple: (Int, Int)): Position = Position(column = tuple._1, row = tuple._2)
}
