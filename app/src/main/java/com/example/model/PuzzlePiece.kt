package com.example.model

enum class EdgeType {
    FLAT,
    TAB,    // Bulges outward
    SOCKET  // Indents inward
}

data class PieceEdges(
    val top: EdgeType,
    val right: EdgeType,
    val bottom: EdgeType,
    val left: EdgeType,
    // Slight randomized offset factors for true organic irregularity
    val topTabOffset: Float = 0f,
    val rightTabOffset: Float = 0f,
    val bottomTabOffset: Float = 0f,
    val leftTabOffset: Float = 0f
)

data class PuzzlePiece(
    val id: Int, // 1-based index (e.g., #1..#16)
    val row: Int,
    val col: Int,
    val edges: PieceEdges,
    val isRevealed: Boolean = false,
    val assignedQuestion: TrigQuestion,
    val solvedByPlayer: Int = 0, // 1 for P1, 2 for P2 in 2-player mode
    val attempts: Int = 0,
    val hintsUsed: Int = 0,
    val pointsEarned: Int = 0
)

data class PuzzleConfig(
    val id: String,
    val title: String,
    val destinationName: String,
    val description: String,
    val imageResId: Int? = null,
    val customImageUri: String? = null,
    val gridSize: Int = 4, // 3 -> 9 pieces, 4 -> 16 pieces, 5 -> 25 pieces, 6 -> 36 pieces
    val gameMode: GameMode = GameMode.CLASSIC,
    val timeLimitSeconds: Int = 300, // 5 minutes for timed mode
    val allowHints: Boolean = true,
    val maxHintsPerQuestion: Int = 2, // 2 hints available
    val hintPenaltyPoints: Int = 5,
    val allowFormulaCard: Boolean = true,
    val questions: List<TrigQuestion> = emptyList()
) {
    val totalPieces: Int
        get() = gridSize * gridSize
}
