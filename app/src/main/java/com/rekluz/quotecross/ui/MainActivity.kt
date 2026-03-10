package com.rekluz.quotecross

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.rekluz.quotecross.ui.MainContent
import com.rekluz.quotecross.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    // Initializing the ViewModel which keeps the game state
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the core Compose container
        setContent {
            // Passing the viewModel to our UI components
            MainContent(viewModel)
        }
    }
}