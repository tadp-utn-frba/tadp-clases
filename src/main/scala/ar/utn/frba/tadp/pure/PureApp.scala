package ar.utn.frba.tadp.pure

import ar.utn.frba.tadp.model._
import zio.Console._
import zio._

import java.io.IOException
import scala.language.postfixOps

object PureApp extends ZIOAppDefault {
  /**
   * inicializar un juego vacío
   * elegir el jugador humano y la ia
   * seleccionar que jugador empieza
   * mostrar que jugador empieza
   * ejecutar un turno -> repetir hasta que el juego esté completo
   * determinar ganador
   */
  override def run: Task[Unit] = for {
    humanSymbol <- selectHumanSymbol()

    humanPlayer = HumanPlayer(humanSymbol)
    iaPlayer = humanPlayer.other
    startingBoard = Board.empty

    startingPlayer <- startingPlayer(humanPlayer, iaPlayer)
    firstTurn = Turn(startingBoard, startingPlayer, startingPlayer.other)

    _ <- printLine(s"Starts player $startingPlayer!")
    lastTurn <- ZIO.iterate(firstTurn)(_.board.ongoing)(playTurn)
    _ <- printLine(lastTurn.board.winner.toString)

    _ <- printBoard(lastTurn.board)
    gameResult = lastTurn.board.winner.fold("Tied :(")(winner => s"$winner won!")
    _ <- printLine(s"Game ended: $gameResult")
  } yield ()

  def playTurn(turn: Turn): UIO[Turn] = printBoard(turn.board).orDie *> {
    val nextBoard = turn.currentPlayer match {
      case player@HumanPlayer(_) => humanMove(player)(turn.board)
      case player@IAPlayer(_) => iaMove(player)(turn.board)
    }
    nextBoard.map(turn.nextTurn)
  }

  // Human actions
  def selectHumanSymbol(): UIO[SquareValue] = retryUserAction {
    readUserInput("Select human player (X or O): ")(parseSymbol)
  }

  def humanMove(player: Player)(board: Board): UIO[Board] = retryUserAction {
    for {
      move <- readUserInput("Select square (1-9): ")(parseMove)
      updatedBoard <- attemptHumanAction(board.play(move, player))
    } yield updatedBoard
  }


  // IA calculations
  def startingPlayer(player1: Player, player2: Player): UIO[Player] = Random.nextBoolean.map(if (_) player1 else player2)

  def iaMove(player: Player)(board: Board): UIO[Board] = for {
    emptySquares <- ZIO.succeed(board.emptySquares)
    randomSquare <- Random.nextIntBounded(emptySquares.length)
    move <- ZIO.succeed(emptySquares(randomSquare).position)
  } yield board.play(move, player)


  // Reading
  private def attemptHumanAction[T](action: => T): IO[WrongUserInputException, T] = ZIO
    .attempt(action)
    .refineOrDie { case e: WrongUserInputException => e }

  private def parseSymbol(symbolInput: String): IO[WrongUserInputException, SquareValue] = attemptHumanAction(SquareValue(symbolInput))

  private def parseMove(moveInput: String): IO[WrongUserInputException, Position] = ZIO
    .attempt(Square.fromSquareNumber(moveInput.toInt))
    .refineOrDie { case _: NoSuchElementException | _: NumberFormatException => WrongUserInputException("Move must be between 1 and 9") }

  private def readUserInput[T](message: String)(parseF: String => IO[WrongUserInputException, T]): IO[WrongUserInputException, T] =
    (print(message) *> readLine).orDie.flatMap(parseF)

  private def retryUserAction[T](inputIO: IO[WrongUserInputException, T]): UIO[T] = inputIO
    .onError(_.failureOption.fold(printRetryMessage("Invalid option"))(e => printRetryMessage(e.message)))
    .refineOrDie { case e: WrongUserInputException => e }
    .retryWhile(_ => true)
    .orDie


  // Printing
  /**
   * Board format (number represents Square index):
   * __1__║__2__║__3__
   * ═════╬═════╬═════
   * __4__║__5__║__6__
   * ═════╬═════╬═════
   * __7__║__8__║__9__
   */
  private def printBoard(board: Board): IO[IOException, Unit] = List(0, 1, 2)
    .map(printRow(board))
    .reduce { (previousLines, currentLine) =>
      previousLines *> printLine(boardLineSeparator) *> currentLine
    } *> printLine("")

  private def printRow(board: Board)(rowNumber: Int): IO[IOException, Unit] =
    printLine(board.orderedRows(rowNumber).mkString(squareSeparator))

  private def printRetryMessage(explanation: String): UIO[Unit] = printLine(s"$explanation, please try again!").orDie
}
