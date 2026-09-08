package com.example.repository

import android.util.Log
import com.example.BuildConfig
import com.example.model.Difficulty
import com.example.model.QuestionCategory
import com.example.model.TriangleOrientation
import com.example.model.TriangleParams
import com.example.model.TriangleSide
import com.example.model.TrigQuestion
import com.example.model.TrigRatio
import com.example.network.GeminiApiService
import com.example.network.GeminiContent
import com.example.network.GeminiGenerateContentRequest
import com.example.network.GeminiGenerationConfig
import com.example.network.GeminiPart
import com.example.network.GeminiQuestionJsonDto
import com.example.trig.QuestionGenerator
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlin.math.atan
import kotlin.math.roundToInt

class GeminiTrigQuestionRepository(
    private val apiService: GeminiApiService = GeminiApiService.create(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val model: String = GeminiApiService.DEFAULT_MODEL
) : TrigQuestionRepository {

    private val tag = "GeminiTrigRepo"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, GeminiQuestionJsonDto::class.java)
    private val jsonAdapter = moshi.adapter<List<GeminiQuestionJsonDto>>(listType)

    override fun isApiKeyAvailable(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    override suspend fun generateQuestions(
        difficulty: Difficulty,
        category: QuestionCategory,
        count: Int,
        targetRatios: List<TrigRatio>,
        fallbackOnFailure: Boolean
    ): Result<List<TrigQuestion>> = withContext(ioDispatcher) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!isApiKeyAvailable()) {
            Log.w(tag, "Gemini API key is not configured in Secrets panel / BuildConfig.")
            if (fallbackOnFailure) {
                Log.i(tag, "Generating $count questions locally using algorithm fallback.")
                val localQuestions = QuestionGenerator.generateQuestionSet(count, targetRatios, difficulty)
                return@withContext Result.success(localQuestions)
            } else {
                return@withContext Result.failure(
                    IllegalStateException("GEMINI_API_KEY is missing. Please set your Gemini API key in the AI Studio Secrets panel.")
                )
            }
        }

        try {
            val systemPrompt = buildSystemInstruction()
            val userPrompt = buildUserPrompt(difficulty, category, count, targetRatios)

            val request = GeminiGenerateContentRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt))
                ),
                generationConfig = GeminiGenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.35f,
                    topP = 0.95f
                )
            )

            val response = apiService.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )

            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (responseText.isNullOrBlank()) {
                throw IllegalStateException("Gemini API returned an empty text candidate.")
            }

            val parsedDtos = parseJsonCandidates(responseText)
            if (parsedDtos.isEmpty()) {
                throw IllegalStateException("Could not parse valid question items from response JSON.")
            }

            val questions = parsedDtos.mapIndexed { index, dto ->
                mapDtoToQuestion(dto, index, difficulty, category, targetRatios)
            }

            Log.i(tag, "Successfully generated ${questions.size} trigonometry questions via Gemini model $model.")
            Result.success(questions)
        } catch (e: Exception) {
            Log.e(tag, "Failed to generate questions with Gemini API: ${e.message}", e)
            if (fallbackOnFailure) {
                Log.i(tag, "Falling back to local question generator.")
                val fallbackQuestions = QuestionGenerator.generateQuestionSet(count, targetRatios, difficulty)
                Result.success(fallbackQuestions)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun generateSingleQuestion(
        difficulty: Difficulty,
        ratio: TrigRatio,
        category: QuestionCategory,
        fallbackOnFailure: Boolean
    ): Result<TrigQuestion> = withContext(ioDispatcher) {
        val result = generateQuestions(
            difficulty = difficulty,
            category = category,
            count = 1,
            targetRatios = listOf(ratio),
            fallbackOnFailure = fallbackOnFailure
        )

        result.mapCatching { questions ->
            questions.firstOrNull()
                ?: throw NoSuchElementException("No questions returned by generator.")
        }
    }

    private fun parseJsonCandidates(raw: String): List<GeminiQuestionJsonDto> {
        val clean = cleanJson(raw)

        // Attempt 1: Moshi direct list adapter
        try {
            val list = jsonAdapter.fromJson(clean)
            if (!list.isNullOrEmpty()) return list
        } catch (e: Exception) {
            Log.d(tag, "Direct Moshi list parse failed: ${e.message}. Trying object wrapper / JSON fallback.")
        }

        // Attempt 2: Wrapped in JSON object {"questions": [...]}
        try {
            val rootObj = JSONObject(clean)
            val arrayKeys = listOf("questions", "problems", "items", "data")
            for (key in arrayKeys) {
                if (rootObj.has(key)) {
                    val arr = rootObj.getJSONArray(key)
                    val fromArray = parseFromJsonArray(arr)
                    if (fromArray.isNotEmpty()) return fromArray
                }
            }
        } catch (_: Exception) { }

        // Attempt 3: Native JSONArray parsing
        try {
            val arr = JSONArray(clean)
            return parseFromJsonArray(arr)
        } catch (e: Exception) {
            Log.e(tag, "All JSON parse attempts failed: ${e.message}")
        }

        return emptyList()
    }

    private fun parseFromJsonArray(arr: JSONArray): List<GeminiQuestionJsonDto> {
        val list = mutableListOf<GeminiQuestionJsonDto>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val optionsList = obj.optJSONArray("options")?.let { optArr ->
                (0 until optArr.length()).map { optArr.optString(it) }
            }
            val acceptableList = obj.optJSONArray("acceptableAnswers")?.let { accArr ->
                (0 until accArr.length()).map { accArr.optString(it) }
            }

            list.add(
                GeminiQuestionJsonDto(
                    id = obj.optString("id").takeIf { it.isNotBlank() },
                    ratio = obj.optString("ratio", "SINE"),
                    category = obj.optString("category"),
                    difficulty = obj.optString("difficulty"),
                    title = obj.optString("title", "Trigonometry Problem"),
                    problemText = obj.optString("problemText", ""),
                    opposite = if (obj.has("opposite")) obj.optDouble("opposite") else null,
                    adjacent = if (obj.has("adjacent")) obj.optDouble("adjacent") else null,
                    hypotenuse = if (obj.has("hypotenuse")) obj.optDouble("hypotenuse") else null,
                    thetaDegrees = if (obj.has("thetaDegrees")) obj.optDouble("thetaDegrees") else null,
                    unknownSide = obj.optString("unknownSide").takeIf { it.isNotBlank() },
                    orientation = obj.optString("orientation").takeIf { it.isNotBlank() },
                    options = optionsList,
                    correctAnswer = obj.optString("correctAnswer", ""),
                    acceptableAnswers = acceptableList,
                    hint1 = obj.optString("hint1", "Recall the definition of the trigonometric ratio."),
                    hint2 = obj.optString("hint2", "Set up the equation using the given side values."),
                    explanation = obj.optString("explanation", "Apply standard trigonometric formulas."),
                    points = if (obj.has("points")) obj.optInt("points") else null
                )
            )
        }
        return list
    }

    private fun mapDtoToQuestion(
        dto: GeminiQuestionJsonDto,
        index: Int,
        defaultDifficulty: Difficulty,
        defaultCategory: QuestionCategory,
        targetRatios: List<TrigRatio>
    ): TrigQuestion {
        val ratio = findRatio(dto.ratio) ?: targetRatios.getOrNull(index % targetRatios.size) ?: TrigRatio.SINE
        val category = findCategory(dto.category) ?: defaultCategory
        val difficulty = findDifficulty(dto.difficulty) ?: defaultDifficulty

        val triangleParams = buildTriangleParams(dto)

        val id = dto.id ?: "gemini_q_${System.currentTimeMillis()}_$index"
        val title = dto.title.ifBlank { "${ratio.displayName} Problem" }
        val problemText = dto.problemText.ifBlank {
            "Determine the value for ${ratio.displayName} in the given right triangle."
        }

        val rawCorrectAnswer = dto.correctAnswer.trim().ifBlank { "3/5" }
        val simplifiedCorrect = com.example.trig.FractionHelper.simplifyFractionString(rawCorrectAnswer)
        val acceptableList = mutableListOf<String>()
        if (rawCorrectAnswer != simplifiedCorrect) {
            acceptableList.add(rawCorrectAnswer)
        }
        dto.acceptableAnswers?.let { acceptableList.addAll(it) }

        // Automatically synthesize standard fractional/decimal equivalents if not present
        if (simplifiedCorrect.contains("/")) {
            try {
                val parts = simplifiedCorrect.split("/")
                val num = parts[0].trim().toDouble()
                val den = parts[1].trim().toDouble()
                if (den != 0.0) {
                    val dec = num / den
                    val decStr = String.format("%.2f", dec)
                    if (!acceptableList.contains(decStr)) acceptableList.add(decStr)
                    val decSingle = String.format("%.1f", dec)
                    if (decSingle.endsWith(".0")) acceptableList.add(decSingle.substringBefore("."))
                    else if (!acceptableList.contains(decSingle)) acceptableList.add(decSingle)
                }
            } catch (_: Exception) {}
        }

        val rawQuestion = TrigQuestion(
            id = id,
            ratio = ratio,
            category = category,
            difficulty = difficulty,
            title = title,
            problemText = problemText,
            triangleParams = triangleParams,
            options = dto.options?.map { com.example.trig.FractionHelper.simplifyFractionString(it) }?.takeIf { it.isNotEmpty() },
            correctAnswer = simplifiedCorrect,
            acceptableAnswers = acceptableList,
            hint1 = dto.hint1.ifBlank { "Consider the mnemonic: ${ratio.mnemonic}" },
            hint2 = dto.hint2.ifBlank { "Use the formula: ${ratio.formulaText}" },
            explanation = dto.explanation.ifBlank { "By definition, ${ratio.formulaText}." },
            points = dto.points ?: difficulty.basePoints
        )
        return rawQuestion.copy(options = com.example.trig.QuestionGenerator.ensureOptions(rawQuestion))
    }

    private fun buildTriangleParams(dto: GeminiQuestionJsonDto): TriangleParams? {
        val opp = dto.opposite
        val adj = dto.adjacent
        val hyp = dto.hypotenuse

        if (opp != null && adj != null && hyp != null && opp > 0.0 && adj > 0.0 && hyp > 0.0) {
            val theta = dto.thetaDegrees?.takeIf { it in 1.0..89.0 } ?: run {
                val calculated = atan(opp / adj) * 180.0 / Math.PI
                ((calculated * 100).roundToInt() / 100.0).coerceIn(5.0, 85.0)
            }

            val unknownSide = dto.unknownSide?.let { sideStr ->
                when {
                    sideStr.contains("OPPOSITE", ignoreCase = true) -> TriangleSide.OPPOSITE
                    sideStr.contains("ADJACENT", ignoreCase = true) -> TriangleSide.ADJACENT
                    sideStr.contains("HYPOTENUSE", ignoreCase = true) -> TriangleSide.HYPOTENUSE
                    else -> null
                }
            }

            val orientation = when {
                dto.orientation?.contains("LEFT", ignoreCase = true) == true -> TriangleOrientation.BOTTOM_LEFT_ANGLE
                dto.orientation?.contains("TOP", ignoreCase = true) == true -> TriangleOrientation.TOP_RIGHT_ANGLE
                else -> TriangleOrientation.BOTTOM_RIGHT_ANGLE
            }

            return TriangleParams(
                opposite = opp,
                adjacent = adj,
                hypotenuse = hyp,
                thetaDegrees = theta,
                unknownSide = unknownSide,
                orientation = orientation
            )
        }
        return null
    }

    private fun findRatio(raw: String?): TrigRatio? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim()
        return TrigRatio.entries.find {
            it.name.equals(clean, ignoreCase = true) ||
            it.abbreviation.equals(clean, ignoreCase = true) ||
            it.displayName.equals(clean, ignoreCase = true)
        }
    }

    private fun findCategory(raw: String?): QuestionCategory? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim()
        return QuestionCategory.entries.find {
            it.name.equals(clean, ignoreCase = true) ||
            it.title.equals(clean, ignoreCase = true)
        }
    }

    private fun findDifficulty(raw: String?): Difficulty? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim()
        return Difficulty.entries.find {
            it.name.equals(clean, ignoreCase = true) ||
            it.label.equals(clean, ignoreCase = true)
        }
    }

    private fun cleanJson(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```json")) {
            text = text.removePrefix("```json").trim()
        } else if (text.startsWith("```")) {
            text = text.removePrefix("```").trim()
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```").trim()
        }
        return text.trim()
    }

    private fun buildSystemInstruction(): String {
        return """
            You are an expert high school mathematics teacher and trigonometry curriculum creator.
            Your task is to generate mathematically verified, pedagogically rich right-triangle trigonometry problems.

            Mathematical Rules to strictly follow:
            1. All triangles are right-angled triangles.
            2. The acute angle theta (θ) satisfies: 0 < θ < 90°.
            3. Formulas:
               - sin θ = Opposite / Hypotenuse
               - cos θ = Adjacent / Hypotenuse
               - tan θ = Opposite / Adjacent
               - csc θ = Hypotenuse / Opposite = 1 / sin θ
               - sec θ = Hypotenuse / Adjacent = 1 / cos θ
               - cot θ = Adjacent / Opposite = 1 / tan θ
            4. The Pythagorean theorem must hold: Opposite² + Adjacent² = Hypotenuse². Use standard Pythagorean triples whenever possible (e.g., 3-4-5, 5-12-13, 8-15-17, 7-24-25, 9-40-41) or clean multiples.
            5. Provide two-tiered progressive hints:
               - hint1: Tier 1 conceptual hint (mnemonic SOH-CAH-TOA, definition, or reciprocal relation).
               - hint2: Tier 2 calculation hint with specific numerical setup.
            6. Provide a detailed step-by-step explanation.
            7. All questions MUST be of MULTIPLE CHOICE type with exactly 4 distinct choices in the "options" array. One of the options MUST match the "correctAnswer" exactly, while the other 3 must be plausible mathematical distractors (e.g. inverted fractions, alternative trigonometric ratios, or adjacent angles).
            8. Return ONLY a valid JSON array of objects without markdown formatting outside the JSON.
        """.trimIndent()
    }

    private fun buildUserPrompt(
        difficulty: Difficulty,
        category: QuestionCategory,
        count: Int,
        targetRatios: List<TrigRatio>
    ): String {
        val ratioList = targetRatios.joinToString(", ") { "${it.displayName} (${it.abbreviation})" }
        val categoryGuidance = when (category) {
            QuestionCategory.IDENTIFY_RATIO -> "Ask which trigonometric ratio or side ratio corresponds to the given sides or fraction."
            QuestionCategory.FIND_RATIO -> "Provide side lengths of a right triangle and ask for the exact simplified fraction or decimal value of the ratio."
            QuestionCategory.FIND_SIDE -> "Give one side length and a trigonometric ratio or angle, asking to calculate the unknown side. Set unknownSide to 'OPPOSITE', 'ADJACENT', or 'HYPOTENUSE'."
            QuestionCategory.FIND_ANGLE -> "Give the side lengths or ratio value and ask to find the acute angle measure in degrees."
            QuestionCategory.RECIPROCAL -> "Focus on reciprocal identities: csc θ = 1/sin θ, sec θ = 1/cos θ, cot θ = 1/tan θ."
            QuestionCategory.MIXED_CHALLENGE -> "Provide real-world situational right triangle problems (e.g., ladders, towers, shadows, ramps) requiring trigonometric ratio application."
        }

        return """
            Generate $count high-quality MULTIPLE-CHOICE right-triangle trigonometry questions.
            - ALL questions must be multiple-choice type with exactly 4 choices in "options".
            - Difficulty Level: ${difficulty.name} (${difficulty.label})
            - Category: ${category.name} (${category.title})
            - Category Focus: $categoryGuidance
            - Ratio Targets to distribute: $ratioList

            Return a JSON array where each object has the following structure:
            [
              {
                "id": "gemini_q_1",
                "ratio": "SINE",
                "category": "${category.name}",
                "difficulty": "${difficulty.name}",
                "title": "Short descriptive title",
                "problemText": "Clear right-triangle trigonometry problem text.",
                "opposite": 6.0,
                "adjacent": 8.0,
                "hypotenuse": 10.0,
                "thetaDegrees": 36.87,
                "unknownSide": "OPPOSITE",
                "options": ["3/5", "4/5", "3/4", "5/3"],
                "correctAnswer": "3/5",
                "acceptableAnswers": ["0.6", "0.60", "6/10"],
                "hint1": "Conceptual mnemonic or formula name (Tier 1)",
                "hint2": "Detailed calculation substitution (Tier 2)",
                "explanation": "Clear step-by-step solution and answer verification.",
                "points": ${difficulty.basePoints}
              }
            ]
        """.trimIndent()
    }
}
