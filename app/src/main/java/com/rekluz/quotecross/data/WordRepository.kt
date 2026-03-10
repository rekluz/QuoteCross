package com.rekluz.quotecross.data // Updated package name

import android.content.Context
import com.rekluz.quotecross.model.HiddenQuoteClue
import com.rekluz.quotecross.model.HiddenQuotePuzzle // Add this import
import kotlinx.serialization.json.Json

class WordRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // --- NEW SECTION: Loads the entire master file ---
    fun loadAllPuzzles(fileName: String): List<HiddenQuotePuzzle> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            // This line tells Kotlin to turn the JSON into a list of Full Puzzles
            json.decodeFromString<List<HiddenQuotePuzzle>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- YOUR EXISTING SECTION ---
    fun loadWords(fileName: String): List<HiddenQuoteClue> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            json.decodeFromString<List<HiddenQuoteClue>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}