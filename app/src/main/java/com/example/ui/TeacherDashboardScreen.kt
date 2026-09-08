package com.example.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.model.PuzzleConfig
import com.example.model.QuestionCategory
import com.example.model.StudentResult
import com.example.model.TrigQuestion
import com.example.model.TrigRatio
import com.example.repository.LocalPuzzleRepository
import com.example.viewmodel.AppTab
import com.example.viewmodel.GameUiState
import java.io.File
import com.example.viewmodel.GameViewModel

enum class TeacherTab(val title: String) {
    OVERVIEW("Overview"),
    CLASSES("Classes"),
    MY_PUZZLES("My Puzzles"),
    CREATE_PUZZLE("Create"),
    AI_GENERATOR("AI Generator"),
    STUDENT_RESULTS("Results"),
    SETTINGS("Settings")
}

@Composable
fun TeacherDashboardScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    var currentTeacherTab by remember { mutableStateOf(TeacherTab.OVERVIEW) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
    ) {
        // Top Bar
        Surface(
            color = Color(0xFF0F172A),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.setAppTab(AppTab.STUDENT_GAME) },
                        modifier = Modifier.testTag("back_to_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Game",
                            tint = Color(0xFF38BDF8)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Teacher Management Dashboard",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Trig Picture Puzzle Analytics & Control",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.setAppTab(AppTab.STUDENT_GAME) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("launch_game_from_teacher_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = currentTeacherTab.ordinal,
            containerColor = Color(0xFF0F172A),
            contentColor = Color(0xFF38BDF8),
            edgePadding = 12.dp
        ) {
            TeacherTab.entries.forEach { tab ->
                Tab(
                    selected = currentTeacherTab == tab,
                    onClick = { currentTeacherTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (currentTeacherTab == tab) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (currentTeacherTab) {
                TeacherTab.OVERVIEW -> OverviewTab(
                    uiState = uiState,
                    onNavigateToClasses = { currentTeacherTab = TeacherTab.CLASSES }
                )
                TeacherTab.CLASSES -> ClassesTab(
                    classes = uiState.teacherClasses,
                    availablePuzzles = uiState.availablePuzzles,
                    onCreateClass = { name, subj, sec, sched, puzzle, goal, notes, students ->
                        viewModel.createClass(name, subj, sec, sched, puzzle, goal, notes, students)
                    },
                    onDeleteClass = { classId ->
                        viewModel.deleteClass(classId)
                    },
                    onAddStudent = { classId, studentName ->
                        viewModel.addStudentToClass(classId, studentName)
                    },
                    onRemoveStudent = { classId, studentName ->
                        viewModel.removeStudentFromClass(classId, studentName)
                    },
                    onLaunchClassPuzzle = { teacherClass ->
                        viewModel.launchPuzzleForClass(teacherClass)
                    }
                )
                TeacherTab.MY_PUZZLES -> MyPuzzlesTab(
                    puzzles = uiState.availablePuzzles,
                    activePuzzle = uiState.activePuzzle,
                    onSelectPuzzle = { puzzle ->
                        viewModel.loadPuzzle(puzzle)
                        viewModel.setAppTab(AppTab.STUDENT_GAME)
                    },
                    onUploadPhoto = { uri, puzzleId ->
                        viewModel.uploadPhotoForPuzzle(uri, puzzleId)
                    },
                    onSetPresetPhoto = { puzzleId, resId ->
                        viewModel.setPuzzlePresetImage(puzzleId, resId)
                    },
                    onDeletePuzzle = { puzzleId ->
                        viewModel.deletePuzzle(puzzleId)
                    }
                )
                TeacherTab.CREATE_PUZZLE -> CreatePuzzleTab(
                    onCreate = { title, dest, desc, size, customUri, resId ->
                        viewModel.createCustomPuzzle(title, dest, desc, size, customUri, resId)
                        viewModel.setAppTab(AppTab.STUDENT_GAME)
                    }
                )
                TeacherTab.AI_GENERATOR -> AiGeneratorTab(
                    isGenerating = uiState.isAIQuestionGenerating,
                    isGeminiAvailable = uiState.isGeminiAvailable,
                    onGenerate = { count, ratios, diff, cat, onDone ->
                        viewModel.generateAIQuestions(count, ratios, diff, cat, onDone)
                    }
                )
                TeacherTab.STUDENT_RESULTS -> StudentResultsTab(results = uiState.studentResultsHistory)
                TeacherTab.SETTINGS -> SettingsTab()
            }
        }
    }
}

@Composable
private fun OverviewTab(
    uiState: GameUiState,
    onNavigateToClasses: () -> Unit
) {
    val results = uiState.studentResultsHistory
    val classes = uiState.teacherClasses
    val totalStudents = classes.sumOf { it.studentRoster.size }
    val avgScore = if (results.isNotEmpty()) results.map { it.totalScore }.average().toInt() else 1150
    val avgAccuracy = if (results.isNotEmpty()) results.map { it.accuracyPercentage }.average().toInt() else 84
    val mostChallenging = "Cosecant (csc θ)"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- Offline Classroom Section Banner ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "OFFLINE READY",
                                color = Color(0xFF10B981),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${classes.size} Offline Classes",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalStudents students enrolled across sections without internet connection.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onNavigateToClasses,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("overview_manage_classes_button")
                ) {
                    Text("Manage Classes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Classroom Learning Insights",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            text = "Aggregated performance across Six Trigonometric Ratios",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Avg Accuracy", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$avgAccuracy%", fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 22.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Avg Score", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$avgScore", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), fontSize = 22.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Challenging", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cosecant", fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E), fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ratio Mastery Breakdown
        Text(
            text = "Ratio Mastery Benchmark",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val benchmarks = listOf(
                    "Sine (sin)" to 92,
                    "Cosine (cos)" to 88,
                    "Tangent (tan)" to 91,
                    "Cosecant (csc)" to 68,
                    "Secant (sec)" to 79,
                    "Cotangent (cot)" to 74
                )

                benchmarks.forEach { (name, pct) ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "$pct%", color = if (pct >= 80) Color(0xFF34D399) else Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (pct >= 80) Color(0xFF10B981) else Color(0xFFF59E0B),
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyPuzzlesTab(
    puzzles: List<PuzzleConfig>,
    activePuzzle: PuzzleConfig,
    onSelectPuzzle: (PuzzleConfig) -> Unit,
    onUploadPhoto: (android.net.Uri, String) -> Unit,
    onSetPresetPhoto: (String, Int) -> Unit,
    onDeletePuzzle: (String) -> Unit
) {
    var editingPuzzleId by remember { mutableStateOf<String?>(null) }
    var showChangePhotoDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: android.net.Uri? ->
        val targetId = editingPuzzleId
        if (uri != null && targetId != null) {
            onUploadPhoto(uri, targetId)
            showChangePhotoDialog = false
            editingPuzzleId = null
        }
    }

    if (showChangePhotoDialog && editingPuzzleId != null) {
        val targetPuzzle = puzzles.find { it.id == editingPuzzleId }
        AlertDialog(
            onDismissRequest = {
                showChangePhotoDialog = false
                editingPuzzleId = null
            },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = "Change Puzzle Photo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Update photo for \"${targetPuzzle?.title ?: "Puzzle"}\"",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38BDF8),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload New Photo from Device", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = Color(0xFF334155))

                    Text(
                        text = "Or Choose Landmark Preset:",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    val presets = listOf(
                        Triple(R.drawable.img_destination_romblon, "Romblon Sandbar", "Cresta de Gallo"),
                        Triple(R.drawable.img_destination_palawan, "Palawan Lagoons", "El Nido"),
                        Triple(R.drawable.img_destination_mayon, "Mayon Volcano", "Albay, Bicol"),
                        Triple(R.drawable.img_destination_banaue, "Rice Terraces", "Banaue, Ifugao")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { (resId, name, loc) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        editingPuzzleId?.let { pid ->
                                            onSetPresetPhoto(pid, resId)
                                        }
                                        showChangePhotoDialog = false
                                        editingPuzzleId = null
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = resId,
                                        contentDescription = name,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = loc, color = Color(0xFF38BDF8), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangePhotoDialog = false
                        editingPuzzleId = null
                    }
                ) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Curated Jigsaw Puzzles",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )

        puzzles.forEach { puzzle ->
            val isActive = puzzle.id == activePuzzle.id
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = if (isActive) Color(0xFF38BDF8) else Color(0xFF334155),
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Thumbnail
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        ) {
                            val imageModel: Any? = when {
                                !puzzle.customImageUri.isNullOrBlank() -> File(puzzle.customImageUri)
                                puzzle.imageResId != null -> puzzle.imageResId
                                else -> R.drawable.img_destination_romblon
                            }

                            AsyncImage(
                                model = imageModel,
                                contentDescription = puzzle.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            if (!puzzle.customImageUri.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xCC059669),
                                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 6.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "CUSTOM",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = puzzle.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "📍 ${puzzle.destinationName}",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFF334155),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${puzzle.gridSize}x${puzzle.gridSize} (${puzzle.gridSize * puzzle.gridSize} pcs)",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = puzzle.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Change Photo button
                        OutlinedButton(
                            onClick = {
                                editingPuzzleId = puzzle.id
                                showChangePhotoDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (puzzle.id.startsWith("custom_")) {
                                IconButton(
                                    onClick = { onDeletePuzzle(puzzle.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Puzzle",
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectPuzzle(puzzle) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isActive) Color(0xFF38BDF8) else Color(0xFF334155),
                                    contentColor = if (isActive) Color(0xFF0F172A) else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isActive) "Current Active" else "Launch Puzzle", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePuzzleTab(
    onCreate: (title: String, destination: String, description: String, gridSize: Int, customUri: String?, resId: Int?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("Mayon Volcano Wonders") }
    var destination by remember { mutableStateOf("Albay, Bicol") }
    var description by remember { mutableStateOf("Solve right triangle ratios to uncover the world-renowned perfect cone volcano.") }
    var selectedGridSize by remember { mutableIntStateOf(4) }

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var savedImagePath by remember { mutableStateOf<String?>(null) }
    var selectedPresetResId by remember { mutableStateOf<Int?>(R.drawable.img_destination_mayon) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPresetResId = null
            val saved = LocalPuzzleRepository.copyImageToLocalStorage(context, uri)
            savedImagePath = saved
        }
    }

    val presets = listOf(
        Triple(R.drawable.img_destination_romblon, "Romblon Island", "Cresta de Gallo, Romblon"),
        Triple(R.drawable.img_destination_palawan, "Palawan Lagoons", "El Nido, Palawan"),
        Triple(R.drawable.img_destination_mayon, "Mayon Volcano", "Albay, Bicol"),
        Triple(R.drawable.img_destination_banaue, "Rice Terraces", "Banaue, Ifugao")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Create New Trig Puzzle",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            text = "Upload any photo from your device or select a landmark preset",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
        )

        // Photo Upload & Preview Section
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PUZZLE PHOTO / DESTINATION IMAGE",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp
                )

                // Live Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
                ) {
                    val previewModel: Any? = when {
                        selectedImageUri != null -> selectedImageUri
                        savedImagePath != null -> File(savedImagePath!!)
                        selectedPresetResId != null -> selectedPresetResId
                        else -> R.drawable.img_destination_romblon
                    }

                    AsyncImage(
                        model = previewModel,
                        contentDescription = "Selected Puzzle Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        color = Color(0xDD0F172A),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (savedImagePath != null || selectedImageUri != null) "✓ Custom Uploaded Photo" else "✓ Landmark Preset Selected",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Upload Button
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Photo from Device (Gallery)", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Or choose a Philippine destination preset:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                // Preset selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { (resId, presetName, loc) ->
                        val isChosen = selectedPresetResId == resId && savedImagePath == null
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedPresetResId = resId
                                    selectedImageUri = null
                                    savedImagePath = null
                                    title = "$presetName Challenge"
                                    destination = loc
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChosen) Color(0xFF0F172A) else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isChosen) 2.dp else 1.dp,
                                color = if (isChosen) Color(0xFF38BDF8) else Color(0xFF334155)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = resId,
                                    contentDescription = presetName,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = presetName,
                                    color = if (isChosen) Color(0xFF38BDF8) else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Puzzle Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155)
            )
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155)
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description & Instructions") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155)
            )
        )

        Text(
            text = "JIGSAW GRID SIZE",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF59E0B),
            fontSize = 12.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(3 to "3x3 (9 pcs)", 4 to "4x4 (16 pcs)", 5 to "5x5 (25 pcs)", 6 to "6x6 (36 pcs)").forEach { (sz, label) ->
                val isSelected = selectedGridSize == sz
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedGridSize = sz },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color(0xFF0F172A),
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onCreate(title, destination, description, selectedGridSize, savedImagePath, selectedPresetResId)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate & Launch Puzzle", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiGeneratorTab(
    isGenerating: Boolean,
    isGeminiAvailable: Boolean,
    onGenerate: (count: Int, ratios: List<TrigRatio>, diff: Difficulty, category: QuestionCategory, onDone: (List<TrigQuestion>) -> Unit) -> Unit
) {
    var generatedQuestions by remember { mutableStateOf<List<TrigQuestion>>(emptyList()) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.AVERAGE) }
    var selectedCategory by remember { mutableStateOf(QuestionCategory.MIXED_CHALLENGE) }
    var selectedCount by remember { mutableIntStateOf(16) }
    var approvedCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Trigonometry Problem Generator",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            Surface(
                color = if (isGeminiAvailable) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (isGeminiAvailable) "Gemini 3.5 Flash Active" else "Fallback Engine Active",
                    color = if (isGeminiAvailable) Color(0xFF34D399) else Color(0xFFFBBF24),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Text(
            text = "Generate mathematically verified trigonometry problems across all 6 ratios with two-tier hints and diagram parameters.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Configuration Form
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Difficulty", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF38BDF8),
                                selectedLabelColor = Color(0xFF0F172A),
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Ratio Category", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuestionCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color(0xFF0F172A),
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Number of Questions", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(9, 16, 25).forEach { count ->
                        FilterChip(
                            selected = selectedCount == count,
                            onClick = { selectedCount = count },
                            label = { Text("$count questions") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF59E0B),
                                selectedLabelColor = Color(0xFF0F172A),
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onGenerate(selectedCount, TrigRatio.entries, selectedDifficulty, selectedCategory) { questions ->
                            generatedQuestions = questions
                            approvedCount = questions.size
                        }
                    },
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A))
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Problems...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate $selectedCount Problems", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generated Questions Review & Approval
        if (generatedQuestions.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Review Generated Set (${generatedQuestions.size})",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Approved: $approvedCount / ${generatedQuestions.size}",
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            generatedQuestions.take(5).forEachIndexed { index, q ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Problem #${index + 1}: ${q.title}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Ans: ${q.correctAnswer}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = q.problemText, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "💡 Hint 1: ${q.hint1}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentResultsTab(results: List<StudentResult>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Classroom Submission Records",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )

        results.forEach { res ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = res.studentName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text(text = res.puzzleTitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Text(text = "${res.totalScore} pts", fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B), fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Accuracy: ${res.accuracyPercentage}%", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Solved: ${res.solvedQuestions}/${res.totalQuestions}", color = Color(0xFF38BDF8), fontSize = 12.sp)
                        Text(text = "Hints: ${res.hintsUsed}", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Pedagogical Game Rules",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Scoring Rules:", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                Text("• Easy question: 100 points\n• Average question: 125 points\n• Difficult question: 150 points\n• Zero-hint bonus: +25 points\n• Hint deduction: -5 points per hint (waived in Relaxed Mode)", color = Color(0xFFE2E8F0), fontSize = 13.sp)

                HorizontalDivider(color = Color(0xFF334155))

                Text("Tolerance Policy:", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                Text("• Fractions, decimals, and degree angles are normalized.\n• Decimal tolerance: ±0.05.\n• Mathematically equivalent answers are accepted.", color = Color(0xFFE2E8F0), fontSize = 13.sp)
            }
        }
    }
}
