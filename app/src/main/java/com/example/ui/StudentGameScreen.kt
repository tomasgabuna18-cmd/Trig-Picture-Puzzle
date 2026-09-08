package com.example.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.jigsaw.JigsawPuzzleBoard
import com.example.model.GameMode
import com.example.viewmodel.AppTab
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentGameScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    var showModeSelector by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadPhotoForPuzzle(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // --- TOP STATS BAR ---
        TopStatsHeader(
            title = uiState.activePuzzle.title,
            score = uiState.currentScore,
            accuracy = uiState.accuracyPercentage,
            solvedCount = uiState.solvedCount,
            totalPieces = uiState.totalPieces,
            timeElapsed = if (uiState.activePuzzle.gameMode == GameMode.TIMED) uiState.timeRemainingSeconds else uiState.timeElapsedSeconds,
            isTimed = uiState.activePuzzle.gameMode == GameMode.TIMED,
            onOpenFormulaCard = { viewModel.toggleFormulaCard(true) },
            onSwitchToTeacher = { viewModel.setAppTab(AppTab.TEACHER_DASHBOARD) }
        )

        // Two player hotseat bar if in 2-player mode
        if (uiState.activePuzzle.gameMode == GameMode.TWO_PLAYER) {
            TwoPlayerBar(
                currentTurn = uiState.twoPlayerTurn,
                p1Score = uiState.player1Score,
                p2Score = uiState.player2Score,
                p1Pieces = uiState.player1Pieces,
                p2Pieces = uiState.player2Pieces
            )
        }

        // --- SUB-HEADER INSTRUCTION BANNER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    ),
                    RoundedCornerShape(10.dp)
                )
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🧩 Click a puzzle piece to solve its Trig Ratio!",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mode: ${uiState.activePuzzle.gameMode.displayName} | Size: ${uiState.activePuzzle.gridSize}x${uiState.activePuzzle.gridSize} (${uiState.totalPieces} pieces)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        if (uiState.selectedClass != null) {
                            Text(
                                text = " • 🏫 ${uiState.selectedClass.className} (${uiState.selectedClass.classCode})",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFF334155),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { showModeSelector = !showModeSelector }
                ) {
                    Text(
                        text = if (showModeSelector) "Close" else "Options",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Options & Mode Dropdown
        AnimatedVisibility(visible = showModeSelector) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (uiState.teacherClasses.isNotEmpty()) {
                        Text(
                            text = "ACTIVE OFFLINE CLASS",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.teacherClasses.forEach { cls ->
                                val isSelected = uiState.selectedClass?.id == cls.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.selectClass(cls)
                                        showModeSelector = false
                                    },
                                    label = { Text("${cls.className} (${cls.classCode})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF10B981),
                                        selectedLabelColor = Color(0xFF0F172A),
                                        containerColor = Color(0xFF1E293B),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = "GAME MODE",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameMode.entries.forEach { mode ->
                            val isSelected = uiState.activePuzzle.gameMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setGameMode(mode)
                                    showModeSelector = false
                                },
                                label = { Text(mode.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF38BDF8),
                                    selectedLabelColor = Color(0xFF0F172A),
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "PUZZLE GRID SIZE",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(3 to "3x3 (9)", 4 to "4x4 (16)", 5 to "5x5 (25)", 6 to "6x6 (36)").forEach { (sz, label) ->
                            val isSelected = uiState.activePuzzle.gridSize == sz
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setGridSize(sz)
                                    showModeSelector = false
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF59E0B),
                                    selectedLabelColor = Color(0xFF0F172A),
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "DESTINATION & PUZZLE PHOTO",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.availablePuzzles.forEach { puzzle ->
                            val isSelected = uiState.activePuzzle.id == puzzle.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.loadPuzzle(puzzle)
                                    showModeSelector = false
                                },
                                label = {
                                    val destShort = puzzle.destinationName.split(",").firstOrNull() ?: puzzle.title
                                    Text(destShort, fontSize = 11.sp)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF38BDF8),
                                    selectedLabelColor = Color(0xFF0F172A),
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Photo",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Custom Photo to Active Puzzle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- MAIN JIGSAW PUZZLE BOARD ---
        JigsawPuzzleBoard(
            pieces = uiState.pieces,
            gridSize = uiState.activePuzzle.gridSize,
            imageBitmap = uiState.imageBitmap,
            selectedPieceId = uiState.selectedPiece?.id,
            newlyRevealedPieceId = uiState.newlyRevealedPieceId,
            onPieceClicked = { piece -> viewModel.selectPiece(piece) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Quick Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.toggleFormulaCard(true) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("open_formula_card_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFFF59E0B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Formula Card", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.restartGame() },
                modifier = Modifier
                    .weight(0.7f)
                    .testTag("restart_game_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- SIX-RATIO PROGRESS TRACKER ---
        SixRatioTracker(
            ratioStats = uiState.ratioStats,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

    // --- MODALS ---
    // 1. Question Modal
    uiState.selectedPiece?.let { piece ->
        QuestionModal(
            piece = piece,
            feedback = uiState.questionFeedback,
            activeHintTier = uiState.activeHintTier,
            onDismiss = { viewModel.closeQuestionModal() },
            onSubmitAnswer = { ans -> viewModel.submitAnswer(ans) },
            onRequestHint = { viewModel.requestHint() }
        )
    }

    // 2. Formula Reference Card
    if (uiState.isFormulaCardOpen) {
        FormulaCardDialog(
            onDismiss = { viewModel.toggleFormulaCard(false) }
        )
    }

    // 3. Final Reveal Celebration Modal
    if (uiState.isGameCompleted) {
        FinalRevealDialog(
            puzzle = uiState.activePuzzle,
            imageBitmap = uiState.imageBitmap,
            totalScore = uiState.currentScore,
            accuracy = uiState.accuracyPercentage,
            questionsSolved = uiState.solvedCount,
            totalQuestions = uiState.totalPieces,
            hintsUsed = uiState.hintsUsedCount,
            timeElapsedSeconds = uiState.timeElapsedSeconds,
            ratioStats = uiState.ratioStats,
            onPlayAgain = { viewModel.restartGame() },
            onViewTeacherAnalytics = {
                viewModel.setAppTab(AppTab.TEACHER_DASHBOARD)
            }
        )
    }
}

@Composable
private fun TopStatsHeader(
    title: String,
    score: Int,
    accuracy: Int,
    solvedCount: Int,
    totalPieces: Int,
    timeElapsed: Int,
    isTimed: Boolean,
    onOpenFormulaCard: () -> Unit,
    onSwitchToTeacher: () -> Unit
) {
    val minutes = timeElapsed / 60
    val seconds = timeElapsed % 60
    val timeStr = "%02d:%02d".format(minutes, seconds)

    Surface(
        color = Color(0xFF0F172A),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // App Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF38BDF8), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📐", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TRIG PICTURE PUZZLE",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = title,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenFormulaCard,
                        modifier = Modifier.size(36.dp).testTag("header_formula_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Formulas",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onSwitchToTeacher,
                        modifier = Modifier.size(36.dp).testTag("header_teacher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Teacher Dashboard",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeaderStatBadge(label = "SCORE", value = "$score", color = Color(0xFFF59E0B))
                HeaderStatBadge(label = "ACCURACY", value = "$accuracy%", color = Color(0xFF10B981))
                HeaderStatBadge(label = "PIECES", value = "$solvedCount/$totalPieces", color = Color(0xFF38BDF8))
                HeaderStatBadge(
                    label = if (isTimed) "REMAINING" else "TIME",
                    value = timeStr,
                    color = if (isTimed && timeElapsed < 30) Color(0xFFEF4444) else Color(0xFFE2E8F0)
                )
            }
        }
    }
}

@Composable
private fun HeaderStatBadge(label: String, value: String, color: Color) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
