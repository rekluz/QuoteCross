package com.rekluz.pandacrostic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rekluz.pandacrostic.model.PuzzleRepository
import com.rekluz.pandacrostic.viewmodel.GameState
import com.rekluz.pandacrostic.viewmodel.GameViewModel

@Composable
fun MainContent(viewModel: GameViewModel) {
    when (viewModel.currentState) {
        GameState.MENU -> MenuScreen(viewModel)
        GameState.PLAYING -> GameScreen(viewModel)
    }
}

@Composable
fun MenuScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PandaCrostic", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = { viewModel.initLevel(PuzzleRepository.levels3x3.first()) }) {
            Text("Play 3x3 Puzzle")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.initLevel(PuzzleRepository.levels5x5.first()) }) {
            Text("Play 5x5 Puzzle")
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val columns = viewModel.currentLevel?.gridSize ?: 5

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { viewModel.goToMenu() }) { Text("Menu") }
            if (viewModel.isSolved) Text("Solved!", color = Color(0xFF4CAF50), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.tiles.size) { index ->
                val tile = viewModel.tiles[index]
                TileItem(
                    letter = tile.currentLetter,
                    isSelected = viewModel.selectedIndex == index,
                    isCorrect = tile.isCorrect,
                    isBlank = tile.isBlank,
                    onClick = { viewModel.handleTileClick(index) }
                )
            }
        }

        // Simple Clue Section
        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Across:", fontWeight = FontWeight.Bold)
            viewModel.currentLevel?.acrossClues?.forEach { Text("${it.first}: ${it.second}") }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Down:", fontWeight = FontWeight.Bold)
            viewModel.currentLevel?.downClues?.forEach { Text("${it.first}: ${it.second}") }
        }
    }
}

@Composable
fun TileItem(letter: String, isSelected: Boolean, isCorrect: Boolean, isBlank: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        isBlank -> Color.Transparent
        isSelected -> Color.Cyan
        isCorrect -> Color(0xFF81C784)
        else -> Color.LightGray
    }
    Box(
        modifier = Modifier.padding(2.dp).aspectRatio(1f).background(backgroundColor)
            .border(1.dp, if (isBlank) Color.Transparent else Color.Black)
            .clickable(enabled = !isBlank && !isCorrect) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!isBlank) Text(text = letter, fontSize = 18.sp)
    }
}