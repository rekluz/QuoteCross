package com.rekluz.quotecross.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rekluz.quotecross.R
import com.rekluz.quotecross.model.GridPosition
import com.rekluz.quotecross.viewmodel.CellDisplayState
import com.rekluz.quotecross.viewmodel.GameState
import com.rekluz.quotecross.viewmodel.GameViewModel
import android.content.Context
import android.content.pm.PackageManager
@Composable
fun MainContent(viewModel: GameViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F4F0)
    ) {
        when (viewModel.currentState) {
            GameState.MENU -> MenuScreen(viewModel)
            GameState.PLAYING -> GameScreen(viewModel)
        }
    }
}
@Composable
fun MenuScreen(viewModel: GameViewModel) {
    // Pull the version name from your build.gradle.kts automatically
    val context = androidx.compose.ui.platform.LocalContext.current
    val appVersion = getAppVersion(context)

    // Logo Animation Logic
    val infiniteTransition = rememberInfiniteTransition(label = "LogoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        // This pushes the Version Text to the very bottom
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- TOP: Logo and Title ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_logo_circular),
                contentDescription = "QuoteCross Logo",
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "QuoteCross",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }

        // --- MIDDLE: Scrollable Puzzle List ---
        // weight(1f) tells this box to take up all available center space
        Box(modifier = Modifier.weight(1f).padding(vertical = 24.dp)) {
            val puzzleCount = viewModel.getPuzzleCount()
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(puzzleCount) { index ->
                    val puzzleNumber = index + 1
                    val buttonColor = if (index < 2) Color(0xFF4CAF50) else Color(0xFFE65100)
                    MenuButton(text = "Puzzle $puzzleNumber", color = buttonColor) {
                        viewModel.loadPuzzleByIndex(index)
                    }
                }
            }
        }

        // --- BOTTOM: Version Footer ---
        Text(
            text = "v$appVersion",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .width(240.dp)
            .padding(vertical = 6.dp)
    ) {
        Text(text)
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val puzzle = viewModel.currentPuzzle ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Button(onClick = { viewModel.goToMenu() }, modifier = Modifier.padding(end = 4.dp)) {
                    Text("Menu", fontSize = 11.sp)
                }
                OutlinedButton(onClick = { viewModel.clearSelection() }) {
                    Text("Clear", fontSize = 11.sp)
                }
            }
            Text(
                "Words: ${viewModel.solvedWords.size}/${puzzle.placedWords.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF2E7D32)
            )
        }

        // --- GRID SECTION ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val availableWidth = maxWidth
            val availableHeight = maxHeight
            val spacing = 2.dp
            val gridSize = puzzle.gridSize

            val cellSizeByWidth = (availableWidth - (spacing * (gridSize - 1))) / gridSize
            val cellSizeByHeight = (availableHeight - (spacing * (gridSize - 1))) / gridSize

            val finalCellSize = minOf(cellSizeByWidth, cellSizeByHeight, 48.dp)

            HiddenQuoteGridDisplay(viewModel, finalCellSize)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- BOTTOM SECTION (CLUES OR POPUP) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            if (viewModel.showRevealPopup) {
                SolvedPopup(viewModel, puzzle)
            } else {
                ClueList(viewModel)
            }
        }
    }
}

@Composable
fun HiddenQuoteGridDisplay(viewModel: GameViewModel, cellSize: Dp) {
    val puzzle = viewModel.currentPuzzle ?: return
    val spacing = 2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (row in 0 until puzzle.gridSize) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                for (col in 0 until puzzle.gridSize) {
                    val pos = GridPosition(row, col)
                    HiddenQuoteGridCell(
                        letter = puzzle.grid[row][col],
                        displayState = viewModel.getCellDisplayState(pos),
                        isSelected = pos in viewModel.selectedPositions,
                        onClick = { viewModel.selectGridCell(pos) },
                        cellSize = cellSize
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing))
        }
    }
}

@Composable
fun HiddenQuoteGridCell(
    letter: Char,
    displayState: CellDisplayState,
    isSelected: Boolean,
    onClick: () -> Unit,
    cellSize: Dp
) {
    val bgColor = when (displayState) {
        CellDisplayState.BLANK -> Color(0xFFE0E0E0)
        CellDisplayState.QUOTE_REVEALED -> Color(0xFF81C784)
        CellDisplayState.GREYED_OUT -> Color(0xFFD1D1D1)
        else -> if (isSelected) Color(0xFFFFF176) else Color.White
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(bgColor)
            .border(0.5.dp, if (isSelected) Color(0xFFFBC02D) else Color.Gray)
            .clickable(enabled = displayState != CellDisplayState.BLANK) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (letter != '_' && displayState != CellDisplayState.BLANK) {
            Text(
                text = letter.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = if (cellSize < 32.dp) 13.sp else 17.sp,
                color = if (displayState == CellDisplayState.GREYED_OUT) Color.DarkGray else Color.Black
            )
        }
    }
}

@Composable
fun SolvedPopup(viewModel: GameViewModel, puzzle: com.rekluz.quotecross.model.HiddenQuoteGrid) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Puzzle Solved!", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(puzzle.quoteText, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text("— ${puzzle.quoteAttribution}", fontSize = 12.sp, color = Color.Gray)
            Button(onClick = { viewModel.goToMenu() }, modifier = Modifier.padding(top = 12.dp)) {
                Text("Back to Menu")
            }
        }
    }
}

@Composable
fun ClueList(viewModel: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Clues:",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.getUnsolvedClues()) { clue ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(clue.clue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("(${clue.word.length} letters)", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}


fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0.0"
    } catch (e: PackageManager.NameNotFoundException) {
        "1.0.0"
    }
}