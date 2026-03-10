package com.rekluz.quotecross.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.rekluz.quotecross.data.PuzzleRepository
import com.rekluz.quotecross.model.*

enum class GameState { MENU, PLAYING }
// Note: You can keep your UI mapping to CellDisplayState in Components.kt
enum class CellDisplayState { NORMAL, SELECTED, GREYED_OUT, QUOTE_REVEALED, BLANK, EMPTY }

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the Repository using the application context
    private val repository = PuzzleRepository(application)

    var currentState by mutableStateOf(GameState.MENU)
    var currentPuzzle by mutableStateOf<HiddenQuoteGrid?>(null)
    var selectedPositions by mutableStateOf<Set<GridPosition>>(emptySet())
    var solvedWords by mutableStateOf<Set<String>>(emptySet())
    var showRevealPopup by mutableStateOf(false)

    /**
     * Professional way: UI calls this with an index (0, 1, 2...)
     * The ViewModel fetches the data from the Repository.
     */
    fun loadPuzzleByIndex(index: Int) {
        val puzzleData = repository.getPuzzle(index)
        if (puzzleData != null) {
            initPuzzle(puzzleData)
        }
    }

    /**
     * Gets the count of puzzles available in the JSON master file
     */
    fun getPuzzleCount(): Int = repository.getCount()

    fun initPuzzle(puzzleData: HiddenQuotePuzzle) {
        currentPuzzle = HiddenQuoteGridGenerator.generateGrid(puzzleData)
        selectedPositions = emptySet()
        solvedWords = emptySet()
        showRevealPopup = false
        currentState = GameState.PLAYING
    }

    fun selectGridCell(position: GridPosition) {
        val current = currentPuzzle ?: return
        // Do not allow selection of blank filler cells
        if (current.grid[position.row][position.col] == '_') return

        selectedPositions = if (position in selectedPositions) {
            selectedPositions - position
        } else {
            selectedPositions + position
        }
        checkForMatch()
    }

    private fun checkForMatch() {
        val current = currentPuzzle ?: return
        val selected = selectedPositions
        if (selected.isEmpty()) return

        // Check if the current selection exactly matches any unsolved word's positions
        for (placedWord in current.placedWords) {
            val wordPositions = placedWord.positions.toSet()
            if (!placedWord.isSolved && selected == wordPositions) {
                solveWord(placedWord.word)
                return
            }
        }
    }

    private fun solveWord(word: String) {
        val current = currentPuzzle ?: return
        solvedWords = solvedWords + word

        val updatedWords = current.placedWords.map {
            if (it.word == word) it.copy(isSolved = true) else it
        }

        currentPuzzle = current.copy(placedWords = updatedWords)

        // If all words are found, trigger the victory popup
        if (updatedWords.all { it.isSolved }) {
            showRevealPopup = true
        }
        clearSelection()
    }

    fun clearSelection() {
        selectedPositions = emptySet()
    }

    fun goToMenu() {
        currentState = GameState.MENU
        currentPuzzle = null
        showRevealPopup = false
    }

    fun getUnsolvedClues() = currentPuzzle?.placedWords?.filter { !it.isSolved } ?: emptyList()

    fun getCellDisplayState(position: GridPosition): CellDisplayState {
        val current = currentPuzzle ?: return CellDisplayState.EMPTY
        if (current.grid[position.row][position.col] == '_') return CellDisplayState.BLANK

        val isSolved = current.placedWords.any { it.isSolved && position in it.positions }
        val isQuote = position in current.quoteLetter

        return when {
            isQuote && isSolved -> CellDisplayState.QUOTE_REVEALED
            isSolved -> CellDisplayState.GREYED_OUT
            position in selectedPositions -> CellDisplayState.SELECTED
            else -> CellDisplayState.NORMAL
        }
    }
}