package com.rekluz.pandacrostic.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.rekluz.pandacrostic.ui.theme.PandacrosticTheme
import com.rekluz.pandacrostic.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PandacrosticTheme {
                // We call MainContent instead of GameScreen
                // This allows the ViewModel to decide whether to show the Menu or the Game
                MainContent(viewModel)
            }
        }
    }
}