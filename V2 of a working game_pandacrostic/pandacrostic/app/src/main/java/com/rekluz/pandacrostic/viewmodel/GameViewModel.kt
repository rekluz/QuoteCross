package com.rekluz.pandacrostic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rekluz.pandacrostic.model.CrissCrossLevel
import com.rekluz.pandacrostic.model.GridCell

// The different "pages" of your app
enum class GameState {
    MENU,
    PLAYING
}

class GameViewModel : ViewModel() {
    var currentState by mutableStateOf(GameState.MENU)
    var tiles by mutableStateOf<List<GridCell>>(emptyList())
    var selectedIndex by mutableStateOf<Int?>(null)
    var isSolved by mutableStateOf(false)
    var currentLevel: CrissCrossLevel? by mutableStateOf(null)

    fun initLevel(level: CrissCrossLevel) {
        val blank = "_"
        val letterIndices = level.solvedChars.indices.filter { level.solvedChars[it] != blank }
        val shuffledLetters = letterIndices.map { level.solvedChars[it] }.shuffled()

        val tempTiles = level.solvedChars.mapIndexed { index, solvedChar ->
            if (solvedChar == blank) {
                GridCell(currentLetter = blank, solvedLetter = blank)
            } else {
                val shufflePos = letterIndices.indexOf(index)
                GridCell(currentLetter = shuffledLetters[shufflePos], solvedLetter = solvedChar)
            }
        }

        tiles = tempTiles
        currentLevel = level
        selectedIndex = null
        isSolved = false
        currentState = GameState.PLAYING // Switch to game screen
    }

    fun goToMenu() {
        currentState = GameState.MENU
    }

    fun handleTileClick(clickedIdx: Int) {
        val clickedCell = tiles[clickedIdx]
        if (clickedCell.isBlank || clickedCell.isCorrect || isSolved) return

        if (selectedIndex == null) {
            selectedIndex = clickedIdx
        } else if (selectedIndex == clickedIdx) {
            selectedIndex = null
        } else {
            val newList = tiles.toMutableList()
            val cellA = newList[selectedIndex!!]
            val cellB = newList[clickedIdx]

            newList[selectedIndex!!] = cellA.copy(currentLetter = cellB.currentLetter)
            newList[clickedIdx] = cellB.copy(currentLetter = cellA.currentLetter)

            tiles = newList
            selectedIndex = null

            if (tiles.all { it.isCorrect }) {
                isSolved = true
            }
        }
    }
}