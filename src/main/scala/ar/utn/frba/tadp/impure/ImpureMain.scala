package ar.utn.frba.tadp.impure

import ar.utn.frba.tadp.model.{Board, BoardStatus, HumanPlayer, Player, Position, Square, SquareValue, boardLineSeparator}

import scala.annotation.tailrec
import scala.util.Random

object ImpureMain extends App {
  val random = new Random(System.nanoTime())
  val game: Board = Board.empty
  val humanPlayer = HumanPlayer(SquareValue(getStr("Select player (X or O): ")))
  val iaPlayer = humanPlayer.other
  println(s"IA player: $iaPlayer")

  val startPlayer = if (random.nextInt(2) == 1) humanPlayer else iaPlayer
  println(s"Start player: $startPlayer")

  val finishedGame = turn(startPlayer, game)
  println("Game ended: ")
  println(finishedGame.winner.fold("Tied") { winner => s"Winner is $winner" })

  @tailrec
  def turn(player: Player, game: Board): Board = {
    println(toString(game))
    if (game.status == BoardStatus.Finished) {
      game
    } else {
      println(s"$player turn")
      val nextGame: Board =
        if (player == iaPlayer) {
          iaMove(iaPlayer, game)
        } else {
          humanMove(humanPlayer, game)
        }
      turn(player.other, nextGame)
    }
  }

  def getStr(msg: String): String = {
    print(msg)
    scala.io.StdIn.readLine()
  }

  def iaMove(iaPlayer: Player, game: Board): Board = {
    def move(): Position = {
      val emptySquares = game.squares.filter(_.value == SquareValue.Empty)
      emptySquares(random.nextInt(emptySquares.length)).position
    }

    playerMove(move, iaPlayer, game)
  }

  def humanMove(player: Player, game: Board): Board = {
    def move(): Position = {
      val moveStr = getStr("Select square: ")
      parseMove(moveStr)
    }

    playerMove(move, player, game)
  }

  @tailrec
  private def playerMove(move: () => Position, player: Player, game: Board): Board = try {
    game.play(move(), player)
  } catch {
    case _: Throwable => playerMove(move, player, game)
  }

  private def parseMove(moveStr: String): Position = Square.fromSquareNumber(moveStr.toInt)

  /**
   * Board format (number represents Square index):
   * __1__║__2__║__3__
   * ═════╬═════╬═════
   * __4__║__5__║__6__
   * ═════╬═════╬═════
   * __7__║__8__║__9__
   *
   */
  private def toString(board: Board): String =
    s"""|${board.square(0, 0)}║${board.square(1, 0)}║${board.square(2, 0)}
        |$boardLineSeparator
        |${board.square(0, 1)}║${board.square(1, 1)}║${board.square(2, 1)}
        |$boardLineSeparator
        |${board.square(0, 2)}║${board.square(1, 2)}║${board.square(2, 2)}
        |""".stripMargin
}