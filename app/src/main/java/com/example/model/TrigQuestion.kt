package com.example.model

enum class TriangleOrientation {
    BOTTOM_RIGHT_ANGLE, // Base at bottom, right angle at bottom right
    BOTTOM_LEFT_ANGLE,  // Base at bottom, right angle at bottom left
    TOP_RIGHT_ANGLE     // Inverted orientation
}

enum class TriangleSide {
    OPPOSITE,
    ADJACENT,
    HYPOTENUSE
}

data class TriangleParams(
    val opposite: Double,
    val adjacent: Double,
    val hypotenuse: Double,
    val thetaDegrees: Double,
    val unknownSide: TriangleSide? = null,
    val orientation: TriangleOrientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
)

data class TrigQuestion(
    val id: String,
    val ratio: TrigRatio,
    val category: QuestionCategory,
    val difficulty: Difficulty,
    val title: String,
    val problemText: String,
    val triangleParams: TriangleParams? = null,
    val options: List<String>? = null, // If multiple-choice
    val correctAnswer: String,
    val acceptableAnswers: List<String> = emptyList(),
    val hint1: String,
    val hint2: String,
    val explanation: String,
    val points: Int = difficulty.basePoints
)

data class RatioStats(
    val ratio: TrigRatio,
    val attempted: Int = 0,
    val correct: Int = 0
) {
    val accuracyPercentage: Int
        get() = if (attempted > 0) ((correct.toDouble() / attempted) * 100).toInt() else 0
}
