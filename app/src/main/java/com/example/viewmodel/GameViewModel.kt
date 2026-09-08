package com.example.viewmodel

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.jigsaw.JigsawGridGenerator
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.model.PuzzleConfig
import com.example.model.PuzzlePiece
import com.example.model.QuestionCategory
import com.example.model.RatioStats
import com.example.model.StudentResult
import com.example.model.TeacherClass
import com.example.model.TrigQuestion
import com.example.model.TrigRatio
import com.example.repository.GeminiTrigQuestionRepository
import com.example.repository.LocalClassRepository
import com.example.repository.LocalPuzzleRepository
import com.example.repository.TrigQuestionRepository
import com.example.trig.AnswerValidator
import com.example.trig.QuestionGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class GameUiState(
    val activePuzzle: PuzzleConfig,
    val pieces: List<PuzzlePiece> = emptyList(),
    val selectedPiece: PuzzlePiece? = null,
    val newlyRevealedPieceId: Int? = null,
    val currentScore: Int = 0,
    val bestScore: Int = 1250,
    val totalAttempts: Int = 0,
    val hintsUsedCount: Int = 0,
    val timeElapsedSeconds: Int = 0,
    val timeRemainingSeconds: Int = 300,
    val isGameCompleted: Boolean = false,
    val isFormulaCardOpen: Boolean = false,
    val activeTab: AppTab = AppTab.STUDENT_GAME,
    val imageBitmap: ImageBitmap? = null,
    // Two player stats
    val twoPlayerTurn: Int = 1, // 1 or 2
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val player1Pieces: Int = 0,
    val player2Pieces: Int = 0,
    // Question Modal state
    val questionFeedback: AnswerFeedback? = null,
    val activeHintTier: Int = 0, // 0 = none, 1 = hint 1, 2 = hint 2
    // Ratio Breakdown
    val ratioStats: Map<TrigRatio, RatioStats> = TrigRatio.entries.associateWith { RatioStats(it) },
    // Teacher Data
    val availablePuzzles: List<PuzzleConfig> = emptyList(),
    val studentResultsHistory: List<StudentResult> = emptyList(),
    val teacherClasses: List<TeacherClass> = emptyList(),
    val selectedClass: TeacherClass? = null,
    val isAIQuestionGenerating: Boolean = false,
    val isGeminiAvailable: Boolean = false
) {
    val solvedCount: Int
        get() = pieces.count { it.isRevealed }

    val totalPieces: Int
        get() = pieces.size

    val accuracyPercentage: Int
        get() = if (totalAttempts > 0) ((solvedCount.toDouble() / totalAttempts) * 100).toInt().coerceIn(0, 100) else 100

    val mostDifficultRatio: String
        get() {
            val lowest = ratioStats.values.filter { it.attempted > 0 }.minByOrNull { it.accuracyPercentage }
            return lowest?.ratio?.displayName ?: "None yet"
        }
}

enum class AppTab {
    STUDENT_GAME,
    TEACHER_DASHBOARD,
    TOPICS_REFERENCE
}

sealed class AnswerFeedback {
    data class Correct(val message: String, val pointsEarned: Int) : AnswerFeedback()
    data class Incorrect(val message: String, val canRetry: Boolean) : AnswerFeedback()
}

class GameViewModel @JvmOverloads constructor(
    application: Application,
    private val questionRepository: TrigQuestionRepository = GeminiTrigQuestionRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun createInitialState(): GameUiState {
        val initialPuzzles = try {
            LocalPuzzleRepository(getApplication()).getPuzzles()
        } catch (_: Exception) {
            emptyList()
        }

        val romblonQuestions = QuestionGenerator.createSampleRomblonQuestions()
        val defaultRomblon = PuzzleConfig(
            id = "sample_romblon",
            title = "Discover the Hidden Destination",
            destinationName = "Cresta de Gallo Island, Romblon",
            description = "Uncover the pristine white sandbars and turquoise waters of Romblon, Philippines by solving 16 trigonometry problems across all 6 ratios.",
            imageResId = R.drawable.img_destination_romblon,
            gridSize = 4,
            gameMode = GameMode.CLASSIC,
            questions = romblonQuestions
        )

        val active = initialPuzzles.firstOrNull() ?: defaultRomblon
        val pieces = initializePieces(active)
        val bitmap = loadBitmapForPuzzle(active)

        // Seed some sample student results for the teacher dashboard analytics
        val sampleResults = listOf(
            StudentResult(
                id = UUID.randomUUID().toString(),
                studentName = "Alex Rivera",
                puzzleTitle = "Discover the Hidden Destination",
                totalScore = 1350,
                accuracyPercentage = 94,
                timeElapsedSeconds = 312,
                totalQuestions = 16,
                solvedQuestions = 16,
                totalAttempts = 17,
                hintsUsed = 2,
                ratioBreakdown = mapOf(
                    "Sine" to 100, "Cosine" to 100, "Tangent" to 100,
                    "Cosecant" to 75, "Secant" to 100, "Cotangent" to 75
                ),
                mostChallengingRatio = "Cosecant"
            ),
            StudentResult(
                id = UUID.randomUUID().toString(),
                studentName = "Maria Santos",
                puzzleTitle = "Discover the Hidden Destination",
                totalScore = 1120,
                accuracyPercentage = 82,
                timeElapsedSeconds = 380,
                totalQuestions = 16,
                solvedQuestions = 16,
                totalAttempts = 21,
                hintsUsed = 4,
                ratioBreakdown = mapOf(
                    "Sine" to 90, "Cosine" to 85, "Tangent" to 92,
                    "Cosecant" to 70, "Secant" to 80, "Cotangent" to 75
                ),
                mostChallengingRatio = "Cosecant"
            ),
            StudentResult(
                id = UUID.randomUUID().toString(),
                studentName = "Juan Dela Cruz",
                puzzleTitle = "Discover the Hidden Destination",
                totalScore = 980,
                accuracyPercentage = 75,
                timeElapsedSeconds = 445,
                totalQuestions = 16,
                solvedQuestions = 15,
                totalAttempts = 24,
                hintsUsed = 6,
                ratioBreakdown = mapOf(
                    "Sine" to 85, "Cosine" to 75, "Tangent" to 80,
                    "Cosecant" to 60, "Secant" to 70, "Cotangent" to 65
                ),
                mostChallengingRatio = "Cosecant"
            )
        )

        val initialClasses = try {
            LocalClassRepository(getApplication()).getClasses()
        } catch (_: Exception) {
            emptyList()
        }

        return GameUiState(
            activePuzzle = active,
            pieces = pieces,
            imageBitmap = bitmap,
            availablePuzzles = if (initialPuzzles.isNotEmpty()) initialPuzzles else listOf(defaultRomblon),
            studentResultsHistory = sampleResults,
            teacherClasses = initialClasses,
            selectedClass = initialClasses.firstOrNull(),
            isGeminiAvailable = questionRepository.isApiKeyAvailable()
        )
    }

    private fun initializePieces(puzzle: PuzzleConfig): List<PuzzlePiece> {
        val gridGenerator = JigsawGridGenerator(puzzle.gridSize)
        val pieces = mutableListOf<PuzzlePiece>()
        var questionIndex = 0

        for (r in 0 until puzzle.gridSize) {
            for (c in 0 until puzzle.gridSize) {
                val pieceId = (r * puzzle.gridSize) + c + 1
                val question = if (questionIndex < puzzle.questions.size) {
                    puzzle.questions[questionIndex]
                } else {
                    QuestionGenerator.generateQuestionSet(1, listOf(TrigRatio.entries[pieceId % 6]))[0]
                }
                questionIndex++

                val edges = gridGenerator.getEdgesForPiece(r, c)
                pieces.add(
                    PuzzlePiece(
                        id = pieceId,
                        row = r,
                        col = c,
                        edges = edges,
                        isRevealed = false,
                        assignedQuestion = question
                    )
                )
            }
        }
        return pieces
    }

    fun loadBitmapForPuzzle(puzzle: PuzzleConfig): ImageBitmap? {
        val context = getApplication<Application>()

        // 1. Try customImageUri if present (local file path or uri string)
        if (!puzzle.customImageUri.isNullOrBlank()) {
            try {
                val file = File(puzzle.customImageUri)
                if (file.exists()) {
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    if (bmp != null) return bmp.asImageBitmap()
                }
            } catch (_: Exception) {}

            try {
                val uri = Uri.parse(puzzle.customImageUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) return bmp.asImageBitmap()
                }
            } catch (_: Exception) {}
        }

        // 2. Try imageResId
        if (puzzle.imageResId != null && puzzle.imageResId != -1) {
            try {
                val bmp = BitmapFactory.decodeResource(context.resources, puzzle.imageResId)
                if (bmp != null) return bmp.asImageBitmap()
            } catch (_: Exception) {}
        }

        // 3. Fallback default
        return try {
            val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.img_destination_romblon)
            bmp?.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    if (state.isGameCompleted) return@update state

                    val newElapsed = state.timeElapsedSeconds + 1
                    val newRemaining = (state.timeRemainingSeconds - 1).coerceAtLeast(0)

                    // Timed game mode expiry
                    if (state.activePuzzle.gameMode == GameMode.TIMED && newRemaining == 0) {
                        state.copy(
                            timeElapsedSeconds = newElapsed,
                            timeRemainingSeconds = 0,
                            isGameCompleted = true
                        )
                    } else {
                        state.copy(
                            timeElapsedSeconds = newElapsed,
                            timeRemainingSeconds = newRemaining
                        )
                    }
                }
            }
        }
    }

    fun selectPiece(piece: PuzzlePiece) {
        if (piece.isRevealed) return
        _uiState.update {
            it.copy(
                selectedPiece = piece,
                questionFeedback = null,
                activeHintTier = 0
            )
        }
    }

    fun closeQuestionModal() {
        _uiState.update {
            it.copy(
                selectedPiece = null,
                questionFeedback = null,
                activeHintTier = 0
            )
        }
    }

    fun submitAnswer(studentAnswer: String) {
        val state = _uiState.value
        val piece = state.selectedPiece ?: return
        val question = piece.assignedQuestion

        val isCorrect = AnswerValidator.isCorrect(
            studentInput = studentAnswer,
            correctAnswer = question.correctAnswer,
            acceptableAnswers = question.acceptableAnswers
        )

        val newAttempts = state.totalAttempts + 1
        val currentRatioStat = state.ratioStats[question.ratio] ?: RatioStats(question.ratio)
        val updatedRatioStats = state.ratioStats.toMutableMap()

        if (isCorrect) {
            // Calculate score: Base Points + (25 Bonus if no hints used)
            val noHintBonus = if (state.activeHintTier == 0) 25 else 0
            val pointsEarned = question.points + noHintBonus

            val newScore = state.currentScore + pointsEarned
            val newBestScore = maxOf(state.bestScore, newScore)

            // Update ratio stats
            updatedRatioStats[question.ratio] = currentRatioStat.copy(
                attempted = currentRatioStat.attempted + 1,
                correct = currentRatioStat.correct + 1
            )

            // Update pieces
            val updatedPieces = state.pieces.map {
                if (it.id == piece.id) {
                    it.copy(
                        isRevealed = true,
                        solvedByPlayer = state.twoPlayerTurn,
                        attempts = it.attempts + 1,
                        hintsUsed = state.activeHintTier,
                        pointsEarned = pointsEarned
                    )
                } else it
            }

            // Two-player update
            val p1Score = if (state.twoPlayerTurn == 1) state.player1Score + pointsEarned else state.player1Score
            val p2Score = if (state.twoPlayerTurn == 2) state.player2Score + pointsEarned else state.player2Score
            val p1Pieces = if (state.twoPlayerTurn == 1) state.player1Pieces + 1 else state.player1Pieces
            val p2Pieces = if (state.twoPlayerTurn == 2) state.player2Pieces + 1 else state.player2Pieces
            // Alternate turn in 2-player mode
            val nextTurn = if (state.activePuzzle.gameMode == GameMode.TWO_PLAYER) {
                if (state.twoPlayerTurn == 1) 2 else 1
            } else 1

            val allSolved = updatedPieces.all { it.isRevealed }

            _uiState.update {
                it.copy(
                    pieces = updatedPieces,
                    currentScore = newScore,
                    bestScore = newBestScore,
                    totalAttempts = newAttempts,
                    ratioStats = updatedRatioStats,
                    newlyRevealedPieceId = piece.id,
                    twoPlayerTurn = nextTurn,
                    player1Score = p1Score,
                    player2Score = p2Score,
                    player1Pieces = p1Pieces,
                    player2Pieces = p2Pieces,
                    isGameCompleted = allSolved,
                    questionFeedback = AnswerFeedback.Correct(
                        message = "Excellent! ${question.explanation}",
                        pointsEarned = pointsEarned
                    )
                )
            }

            // If game completed, auto-record result
            if (allSolved) {
                recordCompletedSession()
            }

            // Close dialog after a pleasant 1.2s delay so student sees success
            viewModelScope.launch {
                delay(1200)
                _uiState.update {
                    it.copy(
                        selectedPiece = null,
                        questionFeedback = null,
                        activeHintTier = 0
                    )
                }
            }
        } else {
            // Incorrect
            updatedRatioStats[question.ratio] = currentRatioStat.copy(
                attempted = currentRatioStat.attempted + 1
            )

            // In two-player mode, failing a question passes the turn to the opponent
            val nextTurn = if (state.activePuzzle.gameMode == GameMode.TWO_PLAYER) {
                if (state.twoPlayerTurn == 1) 2 else 1
            } else state.twoPlayerTurn

            _uiState.update {
                it.copy(
                    totalAttempts = newAttempts,
                    ratioStats = updatedRatioStats,
                    twoPlayerTurn = nextTurn,
                    questionFeedback = AnswerFeedback.Incorrect(
                        message = "Not quite. Check your ratio formula or try a hint!",
                        canRetry = true
                    )
                )
            }
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (state.activeHintTier >= 2) return

        val penalty = if (state.activePuzzle.gameMode != GameMode.RELAXED) {
            state.activePuzzle.hintPenaltyPoints
        } else 0

        _uiState.update {
            it.copy(
                activeHintTier = it.activeHintTier + 1,
                hintsUsedCount = it.hintsUsedCount + 1,
                currentScore = (it.currentScore - penalty).coerceAtLeast(0)
            )
        }
    }

    fun toggleFormulaCard(open: Boolean) {
        _uiState.update { it.copy(isFormulaCardOpen = open) }
    }

    fun setAppTab(tab: AppTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun restartGame() {
        val currentPuzzle = _uiState.value.activePuzzle
        loadPuzzle(currentPuzzle)
    }

    fun loadPuzzle(puzzle: PuzzleConfig) {
        val pieces = initializePieces(puzzle)
        val bitmap = loadBitmapForPuzzle(puzzle)

        _uiState.update {
            it.copy(
                activePuzzle = puzzle,
                pieces = pieces,
                imageBitmap = bitmap,
                currentScore = 0,
                totalAttempts = 0,
                hintsUsedCount = 0,
                timeElapsedSeconds = 0,
                timeRemainingSeconds = puzzle.timeLimitSeconds,
                isGameCompleted = false,
                selectedPiece = null,
                questionFeedback = null,
                activeHintTier = 0,
                twoPlayerTurn = 1,
                player1Score = 0,
                player2Score = 0,
                player1Pieces = 0,
                player2Pieces = 0,
                ratioStats = TrigRatio.entries.associateWith { r -> RatioStats(r) }
            )
        }
        startTimer()
    }

    fun setGameMode(mode: GameMode) {
        val currentPuzzle = _uiState.value.activePuzzle
        val updatedPuzzle = currentPuzzle.copy(gameMode = mode)
        loadPuzzle(updatedPuzzle)
    }

    fun setGridSize(size: Int) {
        val currentPuzzle = _uiState.value.activePuzzle
        val count = size * size
        val questions = QuestionGenerator.generateQuestionSet(count, TrigRatio.entries)
        val updatedPuzzle = currentPuzzle.copy(
            gridSize = size,
            questions = questions
        )
        loadPuzzle(updatedPuzzle)
    }

    private fun recordCompletedSession() {
        val state = _uiState.value
        val ratioMap = state.ratioStats.entries.associate {
            it.key.displayName to it.value.accuracyPercentage
        }
        val result = StudentResult(
            id = UUID.randomUUID().toString(),
            studentName = if (state.activePuzzle.gameMode == GameMode.TWO_PLAYER) "P1 vs P2" else "Current Student",
            puzzleTitle = state.activePuzzle.title,
            totalScore = state.currentScore,
            accuracyPercentage = state.accuracyPercentage,
            timeElapsedSeconds = state.timeElapsedSeconds,
            totalQuestions = state.totalPieces,
            solvedQuestions = state.solvedCount,
            totalAttempts = state.totalAttempts,
            hintsUsed = state.hintsUsedCount,
            ratioBreakdown = ratioMap,
            mostChallengingRatio = state.mostDifficultRatio
        )

        _uiState.update {
            it.copy(studentResultsHistory = listOf(result) + it.studentResultsHistory)
        }
    }

    fun createCustomPuzzle(
        title: String,
        destinationName: String,
        description: String,
        gridSize: Int,
        customImageUri: String? = null,
        imageResId: Int? = null
    ) {
        val totalCount = gridSize * gridSize
        val generatedQuestions = QuestionGenerator.generateQuestionSet(totalCount, TrigRatio.entries, Difficulty.AVERAGE)
        val newPuzzle = PuzzleConfig(
            id = "custom_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom Destination Challenge" },
            destinationName = destinationName.ifBlank { "Hidden Philippine Gem" },
            description = description.ifBlank { "Solve trigonometry problems to reveal the hidden destination." },
            gridSize = gridSize,
            customImageUri = customImageUri,
            imageResId = if (customImageUri.isNullOrBlank()) (imageResId ?: R.drawable.img_destination_romblon) else null,
            questions = generatedQuestions
        )

        val context = getApplication<Application>()
        val updatedPuzzles = try {
            LocalPuzzleRepository(context).savePuzzle(newPuzzle)
        } catch (_: Exception) {
            _uiState.value.availablePuzzles + newPuzzle
        }

        _uiState.update {
            it.copy(availablePuzzles = updatedPuzzles)
        }
        loadPuzzle(newPuzzle)
    }

    /**
     * Uploads and sets a photo from a device Uri for a puzzle (or the active puzzle if puzzleId is null).
     * The photo is copied locally into app internal storage so it is permanently accessible offline.
     */
    fun uploadPhotoForPuzzle(sourceUri: Uri, puzzleId: String? = null) {
        val context = getApplication<Application>()
        val savedPath = LocalPuzzleRepository.copyImageToLocalStorage(context, sourceUri) ?: return
        val targetId = puzzleId ?: _uiState.value.activePuzzle.id
        val puzzleRepo = LocalPuzzleRepository(context)
        val updatedPuzzles = puzzleRepo.updatePuzzleImage(targetId, customImageUri = savedPath, imageResId = null)

        _uiState.update { state ->
            val updatedActive = updatedPuzzles.find { it.id == state.activePuzzle.id } ?: state.activePuzzle
            val nextActive = if (state.activePuzzle.id == targetId) {
                updatedPuzzles.find { it.id == targetId } ?: state.activePuzzle
            } else {
                updatedActive
            }
            state.copy(
                availablePuzzles = updatedPuzzles,
                activePuzzle = nextActive,
                imageBitmap = if (state.activePuzzle.id == targetId) loadBitmapForPuzzle(nextActive) else state.imageBitmap
            )
        }
    }

    /**
     * Switches a puzzle's photo to one of the landmark presets.
     */
    fun setPuzzlePresetImage(puzzleId: String, resId: Int) {
        val context = getApplication<Application>()
        val puzzleRepo = LocalPuzzleRepository(context)
        val updatedPuzzles = puzzleRepo.updatePuzzleImage(puzzleId, customImageUri = null, imageResId = resId)

        _uiState.update { state ->
            val nextActive = if (state.activePuzzle.id == puzzleId) {
                updatedPuzzles.find { it.id == puzzleId } ?: state.activePuzzle
            } else {
                state.activePuzzle
            }
            state.copy(
                availablePuzzles = updatedPuzzles,
                activePuzzle = nextActive,
                imageBitmap = if (state.activePuzzle.id == puzzleId) loadBitmapForPuzzle(nextActive) else state.imageBitmap
            )
        }
    }

    /**
     * Deletes a puzzle and falls back to another available puzzle.
     */
    fun deletePuzzle(puzzleId: String) {
        val context = getApplication<Application>()
        val updatedPuzzles = LocalPuzzleRepository(context).deletePuzzle(puzzleId)
        val nextPuzzle = updatedPuzzles.firstOrNull() ?: _uiState.value.activePuzzle
        _uiState.update {
            it.copy(availablePuzzles = updatedPuzzles)
        }
        if (_uiState.value.activePuzzle.id == puzzleId) {
            loadPuzzle(nextPuzzle)
        }
    }

    /**
     * AI Question Generator powered by Gemini API repository with review & approval workflow
     */
    fun generateAIQuestions(
        count: Int,
        targetRatios: List<TrigRatio>,
        difficulty: Difficulty,
        category: QuestionCategory = QuestionCategory.FIND_RATIO,
        onGenerated: (List<TrigQuestion>) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAIQuestionGenerating = true) }
            val result = questionRepository.generateQuestions(
                difficulty = difficulty,
                category = category,
                count = count,
                targetRatios = targetRatios,
                fallbackOnFailure = true
            )
            _uiState.update { it.copy(isAIQuestionGenerating = false) }
            val questions = result.getOrElse {
                QuestionGenerator.generateQuestionSet(count, targetRatios, difficulty)
            }
            onGenerated(questions)
        }
    }

    // --- Offline Teacher Class Management ---

    private val localClassRepo by lazy { LocalClassRepository(getApplication()) }

    fun createClass(
        className: String,
        subject: String = "Mathematics 9 - Trigonometry",
        section: String = "Section A",
        schedule: String = "MWF 9:00 AM",
        assignedPuzzleTitle: String = "Discover Romblon: Marble Capital",
        targetGoal: Int = 85,
        notes: String = "",
        initialStudents: List<String> = emptyList()
    ) {
        val newClass = TeacherClass(
            className = className.trim(),
            subject = subject.trim().ifBlank { "Mathematics 9 - Trigonometry" },
            section = section.trim().ifBlank { "Section A" },
            schedule = schedule.trim().ifBlank { "MWF 9:00 AM" },
            assignedPuzzleTitle = assignedPuzzleTitle,
            targetAccuracyGoal = targetGoal,
            notes = notes.trim(),
            studentRoster = initialStudents.filter { it.isNotBlank() },
            isOfflineReady = true
        )
        val updatedList = localClassRepo.saveClass(newClass)
        _uiState.update {
            it.copy(
                teacherClasses = updatedList,
                selectedClass = newClass
            )
        }
    }

    fun deleteClass(classId: String) {
        val updatedList = localClassRepo.deleteClass(classId)
        _uiState.update { state ->
            state.copy(
                teacherClasses = updatedList,
                selectedClass = if (state.selectedClass?.id == classId) updatedList.firstOrNull() else state.selectedClass
            )
        }
    }

    fun addStudentToClass(classId: String, studentName: String) {
        val updatedList = localClassRepo.addStudent(classId, studentName)
        _uiState.update { state ->
            state.copy(
                teacherClasses = updatedList,
                selectedClass = updatedList.find { it.id == classId } ?: state.selectedClass
            )
        }
    }

    fun removeStudentFromClass(classId: String, studentName: String) {
        val updatedList = localClassRepo.removeStudent(classId, studentName)
        _uiState.update { state ->
            state.copy(
                teacherClasses = updatedList,
                selectedClass = updatedList.find { it.id == classId } ?: state.selectedClass
            )
        }
    }

    fun selectClass(teacherClass: TeacherClass?) {
        _uiState.update { it.copy(selectedClass = teacherClass) }
    }

    fun updateClassAssignedPuzzle(classId: String, puzzleTitle: String) {
        val updatedList = localClassRepo.updateClassPuzzle(classId, puzzleTitle)
        _uiState.update { state ->
            state.copy(
                teacherClasses = updatedList,
                selectedClass = updatedList.find { it.id == classId } ?: state.selectedClass
            )
        }
    }

    fun launchPuzzleForClass(teacherClass: TeacherClass) {
        val puzzleToLaunch = _uiState.value.availablePuzzles.find {
            it.title.equals(teacherClass.assignedPuzzleTitle, ignoreCase = true)
        } ?: _uiState.value.availablePuzzles.firstOrNull()

        puzzleToLaunch?.let {
            loadPuzzle(it)
            selectClass(teacherClass)
            setAppTab(AppTab.STUDENT_GAME)
        }
    }
}
