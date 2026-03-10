package com.rekluz.quotecross.model

import kotlinx.serialization.Serializable
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.random.Random

@Serializable
data class HiddenQuoteClue(
    val word: String,
    val clue: String
)

@Serializable
data class HiddenQuotePuzzle(
    val quoteText: String,
    val quoteAttribution: String,
    val gridLetters: String,
    val clueWords: List<HiddenQuoteClue>
)

data class GridPosition(
    val row: Int,
    val col: Int
)

enum class Direction {
    HORIZONTAL, VERTICAL, DIAGONAL_DOWN_RIGHT, DIAGONAL_DOWN_LEFT,
    HORIZONTAL_REVERSE, VERTICAL_REVERSE, DIAGONAL_UP_LEFT, DIAGONAL_UP_RIGHT
}

data class PlacedWord(
    val word: String,
    val clue: String,
    val startPos: GridPosition,
    val direction: Direction,
    val positions: List<GridPosition>,
    var isSolved: Boolean = false
)

data class HiddenQuoteGrid(
    val grid: Array<Array<Char>>,
    val gridSize: Int,
    val placedWords: List<PlacedWord>,
    val quoteText: String,
    val quoteAttribution: String,
    val quoteLetter: Set<GridPosition>
)

object HiddenQuoteGridGenerator {
    fun generateGrid(puzzle: HiddenQuotePuzzle): HiddenQuoteGrid {
        val quoteLetters = puzzle.gridLetters.uppercase()
        val gridSize = calculateGridSize(quoteLetters, puzzle.clueWords)

        val grid = Array(gridSize) { Array(gridSize) { ' ' } }
        val usedPositions = mutableSetOf<GridPosition>()
        val quotePositions = mutableSetOf<GridPosition>()
        val random = Random(System.currentTimeMillis())

        // Place quote letters randomly
        for (char in quoteLetters) {
            var placed = false
            var attempts = 0
            while (!placed && attempts < 100) {
                val pos = GridPosition(random.nextInt(gridSize), random.nextInt(gridSize))
                if (pos !in usedPositions) {
                    grid[pos.row][pos.col] = char
                    usedPositions.add(pos)
                    quotePositions.add(pos)
                    placed = true
                }
                attempts++
            }
        }

        val placedWords = mutableListOf<PlacedWord>()
        for (clue in puzzle.clueWords.sortedByDescending { it.word.length }) {
            val word = clue.word.uppercase()
            var placed = false
            var attempts = 0
            while (!placed && attempts < 100) {
                val startPos = GridPosition(random.nextInt(gridSize), random.nextInt(gridSize))
                val direction = Direction.values().random(random)
                val positions = getPositionsForWord(startPos, word, direction, gridSize)

                if (positions != null && canPlaceWord(positions, grid, usedPositions, word)) {
                    positions.forEachIndexed { i, pos ->
                        grid[pos.row][pos.col] = word[i]
                        usedPositions.add(pos)
                    }
                    placedWords.add(PlacedWord(word, clue.clue, startPos, direction, positions))
                    placed = true
                }
                attempts++
            }
        }

        // Fill remaining with blanks
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (grid[r][c] == ' ') grid[r][c] = '_'
            }
        }

        return HiddenQuoteGrid(grid, gridSize, placedWords, puzzle.quoteText, puzzle.quoteAttribution, quotePositions)
    }

    private fun calculateGridSize(quoteLetters: String, clueWords: List<HiddenQuoteClue>): Int {
        val totalLetters = quoteLetters.length + clueWords.sumOf { it.word.length }
        // 1.5x buffer ensures the generator doesn't get "stuck"
        val estimatedSize = ceil(sqrt(totalLetters.toDouble() * 1.5)).toInt()
        return maxOf(estimatedSize, 8)
    }

    private fun getPositionsForWord(start: GridPosition, word: String, dir: Direction, size: Int): List<GridPosition>? {
        val posList = mutableListOf<GridPosition>()
        var r = start.row
        var c = start.col
        for (i in word.indices) {
            if (r !in 0 until size || c !in 0 until size) return null
            posList.add(GridPosition(r, c))
            when (dir) {
                Direction.HORIZONTAL -> c++
                Direction.HORIZONTAL_REVERSE -> c--
                Direction.VERTICAL -> r++
                Direction.VERTICAL_REVERSE -> r--
                Direction.DIAGONAL_DOWN_RIGHT -> { r++; c++ }
                Direction.DIAGONAL_DOWN_LEFT -> { r++; c-- }
                Direction.DIAGONAL_UP_LEFT -> { r--; c-- }
                Direction.DIAGONAL_UP_RIGHT -> { r--; c++ }
            }
        }
        return posList
    }

    private fun canPlaceWord(positions: List<GridPosition>, grid: Array<Array<Char>>, used: Set<GridPosition>, word: String): Boolean {
        return positions.indices.all { i ->
            val pos = positions[i]
            grid[pos.row][pos.col] == ' ' || grid[pos.row][pos.col] == word[i]
        }
    }
}