package ar.utn.frba.tadp

import ar.utn.frba.tadp.model._
//import zio.Console._
//import zio._

/**
 * Note: this is an empty template to start the class
 *
 * - ask player for symbol (X or O) and create it
 * - IA is the other
 * - create initial board
 * - select initial player
 * - initialize first turn
 * - print initial player and board
 * - play until finished (print on each turn)
 * - show result
 */
object TicTacToe {
  def runGame(): Unit = ???

  // Game progress
  def startingPlayer(player1: Player, player2: Player): Player = ???

  def playTurn(turn: Turn): Turn = ???

  // Human actions
  def selectHumanSymbol(): SquareValue = ???

  def humanMove(player: Player)(board: Board): Board = ???

  // IA calculations
  /**
   * - get empty squares
   * - get random between 1-remaining empty squares
   * - get the position at the random square
   * - play the move in the board
   */
  def iaMove(player: Player)(board: Board): Board = ???

  // Reading
  private def parseSymbol(symbolInput: String): SquareValue = ???

  /**
   * Square.fromSquareNumber
   * it may fail with NoSuchElementException or NumberFormatException
   */
  private def parseMove(moveInput: String): Position = ???

  private def readUserInput[T](message: String)(parseF: String => T): T = ???

  // Printing
  /**
   * Board format (number represents Square index):
   * __1__║__2__║__3__
   * ═════╬═════╬═════
   * __4__║__5__║__6__
   * ═════╬═════╬═════
   * __7__║__8__║__9__
   */
  private def printBoard(board: Board): Unit = ???
}
