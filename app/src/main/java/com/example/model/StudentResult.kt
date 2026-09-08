package com.example.model

data class StudentResult(
    val id: String,
    val studentName: String,
    val puzzleTitle: String,
    val dateEpochMs: Long = System.currentTimeMillis(),
    val totalScore: Int,
    val accuracyPercentage: Int,
    val timeElapsedSeconds: Int,
    val totalQuestions: Int,
    val solvedQuestions: Int,
    val totalAttempts: Int,
    val hintsUsed: Int,
    val ratioBreakdown: Map<String, Int> = emptyMap(), // "Sine" -> 100%, etc.
    val mostChallengingRatio: String = "None"
)
