package com.rekluz.quotecross.model

data class GridCell(
    val currentLetter: String,
    val solvedLetter: String,
    val isBlank: Boolean = currentLetter == "_"
) {
    val isCorrect: Boolean
        get() = isBlank || currentLetter == solvedLetter
}