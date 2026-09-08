package com.example.repository

import com.example.model.Difficulty
import com.example.model.QuestionCategory
import com.example.model.TrigQuestion
import com.example.model.TrigRatio

/**
 * Repository interface defining operations for generating trigonometry questions
 * via the Gemini generative model or fallback algorithms based on selected difficulty,
 * ratio category, and specific ratio targets.
 */
interface TrigQuestionRepository {

    /**
     * Generates a batch of trigonometry questions matching the requested difficulty and question category.
     *
     * @param difficulty The difficulty level (EASY, AVERAGE, DIFFICULT).
     * @param category The question category (IDENTIFY_RATIO, FIND_RATIO, FIND_SIDE, FIND_ANGLE, RECIPROCAL, MIXED_CHALLENGE).
     * @param count Number of questions requested (defaults to 4).
     * @param targetRatios The list of trigonometric ratios to distribute or target (defaults to all 6 ratios).
     * @param fallbackOnFailure Whether to return locally generated questions if the API key is missing or call fails.
     * @return Result containing the list of generated TrigQuestion instances, or error details.
     */
    suspend fun generateQuestions(
        difficulty: Difficulty,
        category: QuestionCategory,
        count: Int = 4,
        targetRatios: List<TrigRatio> = TrigRatio.entries,
        fallbackOnFailure: Boolean = true
    ): Result<List<TrigQuestion>>

    /**
     * Generates a single targeted question for a specific trigonometric ratio, difficulty, and category.
     */
    suspend fun generateSingleQuestion(
        difficulty: Difficulty,
        ratio: TrigRatio,
        category: QuestionCategory,
        fallbackOnFailure: Boolean = true
    ): Result<TrigQuestion>

    /**
     * Checks if a valid Gemini API key is currently configured in BuildConfig.
     */
    fun isApiKeyAvailable(): Boolean
}
