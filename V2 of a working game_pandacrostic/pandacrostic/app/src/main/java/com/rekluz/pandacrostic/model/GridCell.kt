package com.rekluz.pandacrostic.model

data class GridCell(
    val currentLetter: String,
    val solvedLetter: String,
    val isBlank: Boolean = currentLetter == "_"
) {
    // A helper to check if this specific tile is in the right spot
    val isCorrect: Boolean
        get() = isBlank || currentLetter == solvedLetter
}