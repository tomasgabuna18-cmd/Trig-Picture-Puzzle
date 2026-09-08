package com.example.trig

import com.example.model.Difficulty
import com.example.model.QuestionCategory
import com.example.model.TriangleOrientation
import com.example.model.TriangleParams
import com.example.model.TriangleSide
import com.example.model.TrigQuestion
import com.example.model.TrigRatio
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.roundToInt
import kotlin.random.Random

object QuestionGenerator {

    /**
     * Default curated 16 questions for "Discover the Hidden Destination" (Romblon Island)
     * Exact distribution: 3 Sine, 3 Cosine, 3 Tangent, 2 Cosecant, 2 Secant, 2 Cotangent, 1 Mixed.
     */
    fun createSampleRomblonQuestions(): List<TrigQuestion> {
        return listOf(
            // --- 1. SINE #1 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q1",
                ratio = TrigRatio.SINE,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.EASY,
                title = "Ratio of Opposite to Hypotenuse",
                problemText = "A right triangle has Opposite = 6 cm, Adjacent = 8 cm, and Hypotenuse = 10 cm. Find the value of sin θ as a simplified fraction or decimal.",
                triangleParams = TriangleParams(
                    opposite = 6.0,
                    adjacent = 8.0,
                    hypotenuse = 10.0,
                    thetaDegrees = 36.87,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("3/5", "4/5", "3/4", "5/3"),
                correctAnswer = "3/5",
                acceptableAnswers = listOf("6/10", "0.6", "0.60"),
                hint1 = "Sine represents the ratio of the Opposite side to the Hypotenuse (SOH).",
                hint2 = "sin θ = Opposite / Hypotenuse = 6 / 10 = 3/5 = 0.6",
                explanation = "sin θ = Opposite / Hypotenuse = 6 / 10 = 3/5 or 0.6."
            ),

            // --- 2. SINE #2 (Category C: Find Unknown Side) ---
            TrigQuestion(
                id = "romblon_q2",
                ratio = TrigRatio.SINE,
                category = QuestionCategory.FIND_SIDE,
                difficulty = Difficulty.AVERAGE,
                title = "Determine the Opposite Side",
                problemText = "In a right triangle with Hypotenuse = 20 cm, sin θ = 3/5. Calculate the length of the Opposite side in cm.",
                triangleParams = TriangleParams(
                    opposite = 12.0,
                    adjacent = 16.0,
                    hypotenuse = 20.0,
                    thetaDegrees = 36.87,
                    unknownSide = TriangleSide.OPPOSITE,
                    orientation = TriangleOrientation.BOTTOM_LEFT_ANGLE
                ),
                options = listOf("12", "16", "20", "15"),
                correctAnswer = "12",
                acceptableAnswers = listOf("12 cm", "12.0"),
                hint1 = "Recall sin θ = Opposite / Hypotenuse. Multiply both sides by the Hypotenuse.",
                hint2 = "Opposite = Hypotenuse × sin θ = 20 × (3/5) = 12 cm.",
                explanation = "Opposite = Hypotenuse × sin θ = 20 × (3/5) = 12 cm."
            ),

            // --- 3. SINE #3 (Category D: Find the Angle) ---
            TrigQuestion(
                id = "romblon_q3",
                ratio = TrigRatio.SINE,
                category = QuestionCategory.FIND_ANGLE,
                difficulty = Difficulty.DIFFICULT,
                title = "Calculate the Reference Angle",
                problemText = "Given that sin θ = 0.5 for an acute angle θ in a right triangle, determine θ in degrees.",
                triangleParams = TriangleParams(
                    opposite = 5.0,
                    adjacent = 8.66,
                    hypotenuse = 10.0,
                    thetaDegrees = 30.0,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("30°", "45°", "60°", "90°"),
                correctAnswer = "30°",
                acceptableAnswers = listOf("30", "30 deg", "30.0"),
                hint1 = "Use the inverse sine function: θ = sin⁻¹(0.5). Which standard special angle has a sine of 1/2?",
                hint2 = "In a 30°-60°-90° right triangle, sin(30°) = 1/2 = 0.5.",
                explanation = "θ = sin⁻¹(0.5) = 30°."
            ),

            // --- 4. COSINE #1 (Category A: Identify the Ratio) ---
            TrigQuestion(
                id = "romblon_q4",
                ratio = TrigRatio.COSINE,
                category = QuestionCategory.IDENTIFY_RATIO,
                difficulty = Difficulty.EASY,
                title = "Identify the Trigonometric Ratio",
                problemText = "In a right triangle, you are given the Adjacent side = 12 and the Hypotenuse = 13. Which primary trigonometric ratio uses these two sides?",
                triangleParams = TriangleParams(
                    opposite = 5.0,
                    adjacent = 12.0,
                    hypotenuse = 13.0,
                    thetaDegrees = 22.62,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("sin θ", "cos θ", "tan θ", "cot θ"),
                correctAnswer = "cos θ",
                acceptableAnswers = listOf("cosine", "cos", "B"),
                hint1 = "Think of the mnemonic SOH CAH TOA.",
                hint2 = "CAH stands for Cosine = Adjacent / Hypotenuse.",
                explanation = "Cosine is defined as Adjacent / Hypotenuse (CAH)."
            ),

            // --- 5. COSINE #2 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q5",
                ratio = TrigRatio.COSINE,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.AVERAGE,
                title = "Compute Cosine of θ",
                problemText = "A right triangle has Opposite = 8, Adjacent = 15, and Hypotenuse = 17. Find cos θ as a fraction.",
                triangleParams = TriangleParams(
                    opposite = 8.0,
                    adjacent = 15.0,
                    hypotenuse = 17.0,
                    thetaDegrees = 28.07,
                    orientation = TriangleOrientation.BOTTOM_LEFT_ANGLE
                ),
                options = listOf("15/17", "8/17", "8/15", "17/15"),
                correctAnswer = "15/17",
                acceptableAnswers = listOf("0.88", "0.882"),
                hint1 = "cos θ = Adjacent / Hypotenuse.",
                hint2 = "Adjacent is 15 and Hypotenuse is 17.",
                explanation = "cos θ = Adjacent / Hypotenuse = 15/17."
            ),

            // --- 6. COSINE #3 (Category C: Find Unknown Side) ---
            TrigQuestion(
                id = "romblon_q6",
                ratio = TrigRatio.COSINE,
                category = QuestionCategory.FIND_SIDE,
                difficulty = Difficulty.DIFFICULT,
                title = "Find Missing Adjacent Side",
                problemText = "If cos θ = 4/5 and the Hypotenuse is 25 cm, calculate the length of the Adjacent side.",
                triangleParams = TriangleParams(
                    opposite = 15.0,
                    adjacent = 20.0,
                    hypotenuse = 25.0,
                    thetaDegrees = 36.87,
                    unknownSide = TriangleSide.ADJACENT,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("20", "15", "25", "12"),
                correctAnswer = "20",
                acceptableAnswers = listOf("20 cm", "20.0"),
                hint1 = "cos θ = Adjacent / Hypotenuse. Therefore, Adjacent = Hypotenuse × cos θ.",
                hint2 = "Adjacent = 25 × (4/5) = 20 cm.",
                explanation = "Adjacent = 25 × (4/5) = 20 cm."
            ),

            // --- 7. TANGENT #1 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q7",
                ratio = TrigRatio.TANGENT,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.EASY,
                title = "Calculate Tangent",
                problemText = "A right triangle has Opposite = 12 cm and Adjacent = 5 cm. Find tan θ as a fraction or decimal.",
                triangleParams = TriangleParams(
                    opposite = 12.0,
                    adjacent = 5.0,
                    hypotenuse = 13.0,
                    thetaDegrees = 67.38,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("12/5", "5/12", "12/13", "5/13"),
                correctAnswer = "12/5",
                acceptableAnswers = listOf("2.4", "2.40"),
                hint1 = "Recall TOA from SOH CAH TOA: Tangent = Opposite / Adjacent.",
                hint2 = "tan θ = 12 / 5 = 2.4.",
                explanation = "tan θ = Opposite / Adjacent = 12 / 5 = 2.4."
            ),

            // --- 8. TANGENT #2 (Category C: Find Unknown Side) ---
            TrigQuestion(
                id = "romblon_q8",
                ratio = TrigRatio.TANGENT,
                category = QuestionCategory.FIND_SIDE,
                difficulty = Difficulty.AVERAGE,
                title = "Find Missing Opposite Side with Tangent",
                problemText = "In a right triangle, tan θ = 3/4 and the Adjacent side is 16 cm. Find the length of the Opposite side.",
                triangleParams = TriangleParams(
                    opposite = 12.0,
                    adjacent = 16.0,
                    hypotenuse = 20.0,
                    thetaDegrees = 36.87,
                    unknownSide = TriangleSide.OPPOSITE,
                    orientation = TriangleOrientation.TOP_RIGHT_ANGLE
                ),
                options = listOf("12", "16", "20", "9"),
                correctAnswer = "12",
                acceptableAnswers = listOf("12 cm", "12.0"),
                hint1 = "tan θ = Opposite / Adjacent. Multiply both sides by Adjacent.",
                hint2 = "Opposite = Adjacent × tan θ = 16 × (3/4) = 12 cm.",
                explanation = "Opposite = 16 × (3/4) = 12 cm."
            ),

            // --- 9. TANGENT #3 (Category D: Find the Angle) ---
            TrigQuestion(
                id = "romblon_q9",
                ratio = TrigRatio.TANGENT,
                category = QuestionCategory.FIND_ANGLE,
                difficulty = Difficulty.DIFFICULT,
                title = "Inverse Tangent Angle",
                problemText = "In an isosceles right triangle, Opposite = 10 cm and Adjacent = 10 cm. Find the acute reference angle θ in degrees.",
                triangleParams = TriangleParams(
                    opposite = 10.0,
                    adjacent = 10.0,
                    hypotenuse = 14.14,
                    thetaDegrees = 45.0,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("45°", "30°", "60°", "90°"),
                correctAnswer = "45°",
                acceptableAnswers = listOf("45", "45 deg", "45.0"),
                hint1 = "tan θ = Opposite / Adjacent = 10 / 10 = 1. What angle has a tangent of 1?",
                hint2 = "θ = tan⁻¹(1) = 45°.",
                explanation = "tan θ = 10/10 = 1, so θ = tan⁻¹(1) = 45°."
            ),

            // --- 10. COSECANT #1 (Category E: Reciprocal Ratios) ---
            TrigQuestion(
                id = "romblon_q10",
                ratio = TrigRatio.COSECANT,
                category = QuestionCategory.RECIPROCAL,
                difficulty = Difficulty.AVERAGE,
                title = "Reciprocal of Sine",
                problemText = "If sin θ = 5/13, determine the value of csc θ (cosecant of θ) as a fraction.",
                triangleParams = TriangleParams(
                    opposite = 5.0,
                    adjacent = 12.0,
                    hypotenuse = 13.0,
                    thetaDegrees = 22.62,
                    orientation = TriangleOrientation.BOTTOM_LEFT_ANGLE
                ),
                options = listOf("13/5", "5/13", "12/13", "13/12"),
                correctAnswer = "13/5",
                acceptableAnswers = listOf("2.6", "2.60"),
                hint1 = "Cosecant is the reciprocal of sine: csc θ = 1 / sin θ.",
                hint2 = "Flip the fraction 5/13 to get 13/5.",
                explanation = "csc θ = 1 / sin θ = Hypotenuse / Opposite = 13 / 5 = 2.6."
            ),

            // --- 11. COSECANT #2 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q11",
                ratio = TrigRatio.COSECANT,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.DIFFICULT,
                title = "Compute Cosecant from Triangle",
                problemText = "A right triangle has Opposite = 7 cm, Adjacent = 24 cm, and Hypotenuse = 25 cm. Find csc θ.",
                triangleParams = TriangleParams(
                    opposite = 7.0,
                    adjacent = 24.0,
                    hypotenuse = 25.0,
                    thetaDegrees = 16.26,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("25/7", "7/25", "24/25", "25/24"),
                correctAnswer = "25/7",
                acceptableAnswers = listOf("3.57", "3.571"),
                hint1 = "csc θ = Hypotenuse / Opposite.",
                hint2 = "Hypotenuse is 25 and Opposite is 7.",
                explanation = "csc θ = Hypotenuse / Opposite = 25 / 7."
            ),

            // --- 12. SECANT #1 (Category E: Reciprocal Ratios) ---
            TrigQuestion(
                id = "romblon_q12",
                ratio = TrigRatio.SECANT,
                category = QuestionCategory.RECIPROCAL,
                difficulty = Difficulty.AVERAGE,
                title = "Reciprocal of Cosine",
                problemText = "If cos θ = 8/17, determine the value of sec θ (secant of θ) as a fraction.",
                triangleParams = TriangleParams(
                    opposite = 15.0,
                    adjacent = 8.0,
                    hypotenuse = 17.0,
                    thetaDegrees = 61.93,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("17/8", "8/17", "15/17", "17/15"),
                correctAnswer = "17/8",
                acceptableAnswers = listOf("2.125", "2.13"),
                hint1 = "Secant is the reciprocal of cosine: sec θ = 1 / cos θ.",
                hint2 = "Invert 8/17 to get 17/8.",
                explanation = "sec θ = 1 / cos θ = Hypotenuse / Adjacent = 17 / 8."
            ),

            // --- 13. SECANT #2 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q13",
                ratio = TrigRatio.SECANT,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.DIFFICULT,
                title = "Calculate Secant from Sides",
                problemText = "In a right triangle with Opposite = 9 cm, Adjacent = 12 cm, and Hypotenuse = 15 cm, find sec θ in simplest fractional form.",
                triangleParams = TriangleParams(
                    opposite = 9.0,
                    adjacent = 12.0,
                    hypotenuse = 15.0,
                    thetaDegrees = 36.87,
                    orientation = TriangleOrientation.BOTTOM_LEFT_ANGLE
                ),
                options = listOf("5/4", "4/5", "3/5", "5/3"),
                correctAnswer = "5/4",
                acceptableAnswers = listOf("15/12", "1.25"),
                hint1 = "sec θ = Hypotenuse / Adjacent = 15 / 12.",
                hint2 = "Simplify 15/12 by dividing numerator and denominator by 3.",
                explanation = "sec θ = Hypotenuse / Adjacent = 15 / 12 = 5/4 = 1.25."
            ),

            // --- 14. COTANGENT #1 (Category E: Reciprocal Ratios) ---
            TrigQuestion(
                id = "romblon_q14",
                ratio = TrigRatio.COTANGENT,
                category = QuestionCategory.RECIPROCAL,
                difficulty = Difficulty.AVERAGE,
                title = "Reciprocal of Tangent",
                problemText = "If tan θ = 4/3, what is the value of cot θ (cotangent of θ)?",
                triangleParams = TriangleParams(
                    opposite = 4.0,
                    adjacent = 3.0,
                    hypotenuse = 5.0,
                    thetaDegrees = 53.13,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("3/4", "4/3", "3/5", "4/5"),
                correctAnswer = "3/4",
                acceptableAnswers = listOf("0.75", "0.750"),
                hint1 = "Cotangent is the reciprocal of tangent: cot θ = 1 / tan θ.",
                hint2 = "Invert 4/3 to obtain 3/4.",
                explanation = "cot θ = 1 / tan θ = Adjacent / Opposite = 3 / 4 = 0.75."
            ),

            // --- 15. COTANGENT #2 (Category B: Find the Ratio) ---
            TrigQuestion(
                id = "romblon_q15",
                ratio = TrigRatio.COTANGENT,
                category = QuestionCategory.FIND_RATIO,
                difficulty = Difficulty.DIFFICULT,
                title = "Find Cotangent from Triangle",
                problemText = "A right triangle has Opposite = 10 cm and Adjacent = 24 cm. Find cot θ as a simplified fraction.",
                triangleParams = TriangleParams(
                    opposite = 10.0,
                    adjacent = 24.0,
                    hypotenuse = 26.0,
                    thetaDegrees = 22.62,
                    orientation = TriangleOrientation.TOP_RIGHT_ANGLE
                ),
                options = listOf("12/5", "5/12", "13/5", "5/13"),
                correctAnswer = "12/5",
                acceptableAnswers = listOf("24/10", "2.4", "2.40"),
                hint1 = "cot θ = Adjacent / Opposite = 24 / 10.",
                hint2 = "Divide 24 and 10 by 2 to get the simplified fraction.",
                explanation = "cot θ = Adjacent / Opposite = 24 / 10 = 12/5 = 2.4."
            ),

            // --- 16. MIXED CHALLENGE #1 (Category F: Comprehensive) ---
            TrigQuestion(
                id = "romblon_q16",
                ratio = TrigRatio.SINE,
                category = QuestionCategory.MIXED_CHALLENGE,
                difficulty = Difficulty.DIFFICULT,
                title = "Pythagorean & Trigonometric Synthesis",
                problemText = "In a right triangle, Adjacent = 8 cm and Hypotenuse = 10 cm. First use the Pythagorean theorem to find the Opposite side, then calculate tan θ.",
                triangleParams = TriangleParams(
                    opposite = 6.0,
                    adjacent = 8.0,
                    hypotenuse = 10.0,
                    thetaDegrees = 36.87,
                    unknownSide = TriangleSide.OPPOSITE,
                    orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                ),
                options = listOf("3/4", "4/3", "3/5", "4/5"),
                correctAnswer = "3/4",
                acceptableAnswers = listOf("6/8", "0.75", "0.750"),
                hint1 = "Opposite² + Adjacent² = Hypotenuse². So Opposite = √(10² - 8²) = √(36) = 6.",
                hint2 = "Once you have Opposite = 6, tan θ = Opposite / Adjacent = 6 / 8 = 3/4.",
                explanation = "Opposite = √(10² - 8²) = 6 cm. Then tan θ = Opposite / Adjacent = 6 / 8 = 3/4."
            )
        )
    }

    /**
     * Procedural generator for any arbitrary number of questions (e.g., 9, 16, 25, 36)
     * across custom ratio distributions and difficulty.
     */
    fun generateQuestionSet(
        count: Int,
        ratios: List<TrigRatio> = TrigRatio.entries,
        difficulty: Difficulty = Difficulty.AVERAGE
    ): List<TrigQuestion> {
        val pythagoreanTriples = listOf(
            Triple(3, 4, 5),
            Triple(5, 12, 13),
            Triple(8, 15, 17),
            Triple(7, 24, 25),
            Triple(6, 8, 10),
            Triple(9, 12, 15),
            Triple(12, 16, 20),
            Triple(10, 24, 26),
            Triple(15, 20, 25),
            Triple(20, 21, 29)
        )

        val questions = mutableListOf<TrigQuestion>()

        for (i in 0 until count) {
            val ratio = ratios[i % ratios.size]
            val triple = pythagoreanTriples[Random.nextInt(pythagoreanTriples.size)]
            val opp = triple.first.toDouble()
            val adj = triple.second.toDouble()
            val hyp = triple.third.toDouble()
            val angle = Math.toDegrees(asin(opp / hyp))

            val orientation = TriangleOrientation.entries[Random.nextInt(TriangleOrientation.entries.size)]
            val qCategory = when (i % 5) {
                0 -> QuestionCategory.FIND_RATIO
                1 -> QuestionCategory.IDENTIFY_RATIO
                2 -> QuestionCategory.FIND_SIDE
                3 -> QuestionCategory.RECIPROCAL
                else -> QuestionCategory.FIND_ANGLE
            }

            val question = buildProceduralQuestion(
                id = "gen_q_${i + 1}",
                index = i + 1,
                ratio = ratio,
                category = qCategory,
                difficulty = difficulty,
                opp = opp,
                adj = adj,
                hyp = hyp,
                angle = angle,
                orientation = orientation
            )
            questions.add(question)
        }

        return questions
    }

    private fun buildProceduralQuestion(
        id: String,
        index: Int,
        ratio: TrigRatio,
        category: QuestionCategory,
        difficulty: Difficulty,
        opp: Double,
        adj: Double,
        hyp: Double,
        angle: Double,
        orientation: TriangleOrientation
    ): TrigQuestion {
        val oppInt = opp.toInt()
        val adjInt = adj.toInt()
        val hypInt = hyp.toInt()

        return when (ratio) {
            TrigRatio.SINE -> {
                val raw = "$oppInt/$hypInt"
                val correct = FractionHelper.simplify(oppInt, hypInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.SINE,
                    category = category,
                    difficulty = difficulty,
                    title = "Sine Ratio Problem #$index",
                    problemText = "A right triangle has Opposite = $oppInt and Hypotenuse = $hypInt. Find the value of sin θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(opp / hyp), "%.3f".format(opp / hyp)),
                    hint1 = "sin θ is the ratio of Opposite to Hypotenuse (SOH).",
                    hint2 = "sin θ = Opposite / Hypotenuse = $oppInt / $hypInt = $correct.",
                    explanation = "sin θ = $oppInt / $hypInt = $correct."
                )
            }
            TrigRatio.COSINE -> {
                val raw = "$adjInt/$hypInt"
                val correct = FractionHelper.simplify(adjInt, hypInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.COSINE,
                    category = category,
                    difficulty = difficulty,
                    title = "Cosine Ratio Problem #$index",
                    problemText = "In a right triangle, Adjacent = $adjInt and Hypotenuse = $hypInt. Calculate cos θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(adj / hyp), "%.3f".format(adj / hyp)),
                    hint1 = "cos θ is the ratio of Adjacent to Hypotenuse (CAH).",
                    hint2 = "cos θ = Adjacent / Hypotenuse = $adjInt / $hypInt = $correct.",
                    explanation = "cos θ = $adjInt / $hypInt = $correct."
                )
            }
            TrigRatio.TANGENT -> {
                val raw = "$oppInt/$adjInt"
                val correct = FractionHelper.simplify(oppInt, adjInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.TANGENT,
                    category = category,
                    difficulty = difficulty,
                    title = "Tangent Ratio Problem #$index",
                    problemText = "A right triangle has Opposite = $oppInt and Adjacent = $adjInt. Find tan θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(opp / adj), "%.3f".format(opp / adj)),
                    hint1 = "tan θ is the ratio of Opposite to Adjacent (TOA).",
                    hint2 = "tan θ = Opposite / Adjacent = $oppInt / $adjInt = $correct.",
                    explanation = "tan θ = $oppInt / $adjInt = $correct."
                )
            }
            TrigRatio.COSECANT -> {
                val raw = "$hypInt/$oppInt"
                val correct = FractionHelper.simplify(hypInt, oppInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.COSECANT,
                    category = category,
                    difficulty = difficulty,
                    title = "Cosecant Ratio Problem #$index",
                    problemText = "Given a right triangle with Opposite = $oppInt and Hypotenuse = $hypInt, determine csc θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(hyp / opp), "%.3f".format(hyp / opp)),
                    hint1 = "Cosecant is the reciprocal of sine: csc θ = Hypotenuse / Opposite.",
                    hint2 = "csc θ = $hypInt / $oppInt = $correct.",
                    explanation = "csc θ = Hypotenuse / Opposite = $hypInt / $oppInt = $correct."
                )
            }
            TrigRatio.SECANT -> {
                val raw = "$hypInt/$adjInt"
                val correct = FractionHelper.simplify(hypInt, adjInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.SECANT,
                    category = category,
                    difficulty = difficulty,
                    title = "Secant Ratio Problem #$index",
                    problemText = "Given a right triangle with Adjacent = $adjInt and Hypotenuse = $hypInt, determine sec θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(hyp / adj), "%.3f".format(hyp / adj)),
                    hint1 = "Secant is the reciprocal of cosine: sec θ = Hypotenuse / Adjacent.",
                    hint2 = "sec θ = $hypInt / $adjInt = $correct.",
                    explanation = "sec θ = Hypotenuse / Adjacent = $hypInt / $adjInt = $correct."
                )
            }
            TrigRatio.COTANGENT -> {
                val raw = "$adjInt/$oppInt"
                val correct = FractionHelper.simplify(adjInt, oppInt)
                TrigQuestion(
                    id = id,
                    ratio = TrigRatio.COTANGENT,
                    category = category,
                    difficulty = difficulty,
                    title = "Cotangent Ratio Problem #$index",
                    problemText = "Given a right triangle with Opposite = $oppInt and Adjacent = $adjInt, determine cot θ in simplest form.",
                    triangleParams = TriangleParams(opp, adj, hyp, angle, orientation = orientation),
                    options = createRatioOptions(correct, oppInt, adjInt, hypInt),
                    correctAnswer = correct,
                    acceptableAnswers = listOf(raw, "%.2f".format(adj / opp), "%.3f".format(adj / opp)),
                    hint1 = "Cotangent is the reciprocal of tangent: cot θ = Adjacent / Opposite.",
                    hint2 = "cot θ = $adjInt / $oppInt = $correct.",
                    explanation = "cot θ = Adjacent / Opposite = $adjInt / $oppInt = $correct."
                )
            }
        }
    }

    private fun createRatioOptions(correctFraction: String, opp: Int, adj: Int, hyp: Int): List<String> {
        val simplifiedCorrect = FractionHelper.simplifyFractionString(correctFraction)
        val pool = listOf(
            FractionHelper.simplify(opp, hyp),
            FractionHelper.simplify(adj, hyp),
            FractionHelper.simplify(opp, adj),
            FractionHelper.simplify(hyp, opp),
            FractionHelper.simplify(hyp, adj),
            FractionHelper.simplify(adj, opp)
        )
        val options = mutableListOf(simplifiedCorrect)
        for (item in pool) {
            if (item != simplifiedCorrect && !options.contains(item)) {
                options.add(item)
            }
            if (options.size == 4) break
        }
        var counter = 1
        while (options.size < 4) {
            val fallback = FractionHelper.simplify(opp + counter, hyp + counter)
            if (!options.contains(fallback)) {
                options.add(fallback)
            }
            counter++
        }
        return options.shuffled()
    }

    /**
     * Ensures that every question has at least 4 multiple-choice options,
     * generating plausible distractors if options are missing or incomplete.
     * All fraction options are guaranteed to be in simplified form.
     */
    fun ensureOptions(question: TrigQuestion): List<String> {
        val simplifiedCorrect = FractionHelper.simplifyFractionString(question.correctAnswer)
        val existing = question.options?.map { FractionHelper.simplifyFractionString(it) }?.distinct()
        if (!existing.isNullOrEmpty() && existing.size >= 4 && existing.contains(simplifiedCorrect)) {
            return existing
        }
        val pool = when (question.ratio) {
            TrigRatio.SINE -> listOf("3/5", "4/5", "3/4", "5/3", "12/13", "5/12", "15/17", "8/17", "30°", "45°", "60°")
            TrigRatio.COSINE -> listOf("4/5", "3/5", "5/4", "12/13", "5/13", "15/17", "8/15", "sin θ", "cos θ", "tan θ", "cot θ")
            TrigRatio.TANGENT -> listOf("3/4", "4/3", "3/5", "4/5", "5/12", "12/5", "8/15", "45°", "30°", "60°")
            TrigRatio.COSECANT -> listOf("5/3", "3/5", "5/4", "13/5", "13/12", "17/8", "25/7", "csc θ", "sec θ", "sin θ", "cot θ")
            TrigRatio.SECANT -> listOf("5/4", "4/5", "5/3", "13/12", "17/8", "25/24", "15/8", "sec θ", "cos θ", "csc θ", "tan θ")
            TrigRatio.COTANGENT -> listOf("4/3", "3/4", "12/5", "5/12", "24/7", "8/15", "15/8", "cot θ", "tan θ", "cos θ", "sin θ")
        }.map { FractionHelper.simplifyFractionString(it) }

        val list = mutableListOf<String>()
        if (existing != null) {
            list.addAll(existing)
        }
        if (!list.contains(simplifiedCorrect)) {
            list.add(0, simplifiedCorrect)
        }
        for (item in pool) {
            if (item != simplifiedCorrect && !list.contains(item)) {
                list.add(item)
            }
            if (list.size == 4) break
        }
        var counter = 1
        while (list.size < 4) {
            val fallback = "${simplifiedCorrect}_$counter"
            list.add(fallback)
            counter++
        }
        return list.shuffled()
    }
}
