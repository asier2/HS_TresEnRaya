package tictactoe

fun main() {
    val game = Game()
    game.startGame()
}

data class Grid(
    val rows: Int = 3,
    val columns: Int = 3,
    var values: MutableList<MutableList<Char>> = mutableListOf(
        mutableListOf('_', '_', '_'),
        mutableListOf('_', '_', '_'),
        mutableListOf('_', '_', '_')
    )
) {

    fun sanitizeCoordinates(nonSanitizedCoordinates: String): Pair<Int, Int> {

        //No separator between coordinates
        if (!nonSanitizedCoordinates.contains(" ")) {
            throw Exception("No separator between coordinates!")
        }
        val (x, y) = nonSanitizedCoordinates.split(" ")

        //Any of the values is not a digit
        if (x.toIntOrNull() == null || y.toIntOrNull() == null) {
            throw Exception("You should enter numbers!")
        }

        //The coordinates are not out of bounds
        if (x.toInt() !in 1..rows || y.toInt() !in 1..columns) {
            throw Exception("Coordinates should be from 1 to 3!")
        }

        return Pair(x.toInt(), y.toInt())
    }

    fun getValueByCoordinates(x: Int, y: Int): Char? {

        //The coordinates are not out of bounds
        if (x !in 1..3 || y !in 1..3) {
            println("Coordinates should be from 1 to 3!")
            return null
        }

        return values[x - 1][y - 1]
    }

    fun setValueInCoordinates(x: Int, y: Int, value: Char) {
        values[x - 1][y - 1] = value
    }

    fun print() {
        println("---------")
        println("| ${values[0][0]} ${values[0][1]} ${values[0][2]} |")
        println("| ${values[1][0]} ${values[1][1]} ${values[1][2]} |")
        println("| ${values[2][0]} ${values[2][1]} ${values[2][2]} |")
        println("---------")

    }

}

class Game {

    var grid = Grid()
    var currentTurn: Player = Player.One

    fun startGame() {
        grid.print()
        do {
            //If the movement was not valid, we don't even check the game state and try to make the move again
            if (makeMove(currentTurn)) {
                currentTurn = currentTurn.next()
            }
            grid.print()
        } while (!checkGameState().isFinished)

        println(checkGameState().message)

    }

    private fun checkGameState(): GameState {

        val gameState = GameState(false)
        var xPoints = 0
        var oPoints = 0
        //All squares are (not) used
        gameState.isFinished = !grid.values.any { it.contains('_') }

        //Check horizontal rows for 'wins'
        for (i in 0..2) {
            val row = grid.values[i]
            if (row.distinct().size == 1) {
                if (row.first() == Player.One.representation) xPoints++
                if (row.first() == Player.Two.representation) oPoints++
            }
        }

        //Check vertical rows for 'wins'
        for (i in 0..2) {
            val row = mutableListOf(grid.values[0][i], grid.values[1][i], grid.values[2][i])
            if (row.distinct().size == 1) {
                if (row.first() == Player.One.representation) xPoints++
                if (row.first() == Player.Two.representation) oPoints++
            }
        }

        //Check diagonals
        val leftDiagonal = mutableListOf(grid.values[0][0], grid.values[1][1], grid.values[2][2])
        if (leftDiagonal.distinct().size == 1) {
            if (leftDiagonal.first() == Player.One.representation) xPoints++
            if (leftDiagonal.first() == Player.Two.representation) oPoints++
        }

        val rightDiagonal = mutableListOf(grid.values[2][0], grid.values[1][1], grid.values[0][2])
        if (rightDiagonal.distinct().size == 1) {
            if (rightDiagonal.first() == Player.One.representation) xPoints++
            if (rightDiagonal.first() == Player.Two.representation) oPoints++
        }

        //Print game states

        //One player made too many moves
        if (kotlin.math.abs(
                grid.values.flatMap { it }.count() { it == Player.One.representation } - grid.values.flatMap { it }
                    .count() { it == Player.Two.representation }) > 1
        ) {
            println("One player has made too many moves!")
            gameState.message = "Impossible"
        }

        //Both scored
        if (xPoints > 0 && oPoints > 0) {
            gameState.message = "Impossible"
        }

        //No one scored
        if (xPoints == 0 && oPoints == 0) {
            if (gameState.isFinished) {
                gameState.message = "Draw"
            } else {
                gameState.message = "Game not finished"
            }
        }

        //X wins
        if (xPoints > 0) {
            gameState.message = "X wins"
            gameState.isFinished = true
        }

        //O wins
        if (oPoints > 0) {
            gameState.message = "O wins"
            gameState.isFinished = true
        }

        println("$xPoints - $oPoints")

        return gameState
    }

    private fun makeMove(player: Player, isRetry: Boolean = false): Boolean {
        if (!isRetry) println("Player $player, let's make a move!")
        val moveCoordinates = readln()
        try {
            val (x: Int, y: Int) = grid.sanitizeCoordinates(moveCoordinates)
            //If the cell we are going to move is NOT empty, error
            if (grid.getValueByCoordinates(x, y) != '_') {
                println("The cell $x $y is already occupied!")
                return false
            }
            grid.setValueInCoordinates(x, y, player.representation)
            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }
}

enum class Player(val representation: Char) {
    One('X'),
    Two('O');

    fun next(): Player {
        return if (this == One) Two else One
    }
}

data class GameState(var isFinished: Boolean, var message: String = "")