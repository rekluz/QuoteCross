package com.rekluz.quotecross.data

import android.content.Context
import com.rekluz.quotecross.model.HiddenQuotePuzzle

class PuzzleRepository(context: Context) {
    // Initialize the loader we just updated
    private val wordRepository = WordRepository(context)

    // This loads the entire list from your Master JSON file immediately
    private val allPuzzles: List<HiddenQuotePuzzle> = wordRepository.loadAllPuzzles("puzzles.json")

    /**
     * Gets a specific puzzle by index (0, 1, 2, etc.)
     */
    fun getPuzzle(index: Int): HiddenQuotePuzzle? {
        return allPuzzles.getOrNull(index)
    }

    /**
     * Returns the total number of puzzles found in the JSON file
     */
    fun getCount(): Int = allPuzzles.size
}