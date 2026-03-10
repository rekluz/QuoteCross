package com.rekluz.pandacrostic.model

// Data class with default values to prevent constructor errors
data class CrissCrossLevel(
    val solvedChars: List<String>,
    val acrossClues: List<Pair<String, String>> = emptyList(),
    val downClues: List<Pair<String, String>> = emptyList(),
    val gridSize: Int = 3
)

object PuzzleRepository {
    val levels3x3: List<CrissCrossLevel> = listOf(
        CrissCrossLevel(
            solvedChars = listOf("C", "A", "T", "O", "R", "E", "W", "E", "T"),
            acrossClues = listOf("R1" to "Feline", "R2" to "Metal source"),
            downClues = listOf("C1" to "Farm animal", "C2" to "Are"),
            gridSize = 3
        )
    )

    val levels5x5: List<CrissCrossLevel> = listOf(
        CrissCrossLevel(
            solvedChars = listOf(
                "S", "T", "A", "R", "S",
                "L", "_", "L", "_", "T",
                "O", "C", "E", "A", "N",
                "P", "_", "X", "_", "D",
                "E", "V", "E", "N", "T"
            ),
            acrossClues = listOf("R1" to "Stars", "R3" to "Ocean", "R5" to "Event"),
            downClues = listOf("C1" to "Slope", "C3" to "Alex", "C5" to "End"),
            gridSize = 5
        )
    )
}