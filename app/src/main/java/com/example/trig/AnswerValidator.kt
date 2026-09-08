package com.example.trig

import kotlin.math.abs

object AnswerValidator {

    /**
     * Validates whether the student's answer matches the expected answer mathematically.
     */
    fun isCorrect(
        studentInput: String,
        correctAnswer: String,
        acceptableAnswers: List<String> = emptyList(),
        tolerance: Double = 0.05
    ): Boolean {
        val cleanedInput = studentInput.trim()
        val cleanedExpected = correctAnswer.trim()

        if (cleanedInput.isEmpty()) return false

        // 1. Direct case-insensitive match (stripping trailing whitespace, degree symbols)
        val normInput = normalizeText(cleanedInput)
        val normExpected = normalizeText(cleanedExpected)
        if (normInput == normExpected) return true

        // 1b. Match by simplified fraction representation (e.g. 6/10 vs 3/5)
        val simpInput = FractionHelper.simplifyFractionString(cleanedInput)
        val simpExpected = FractionHelper.simplifyFractionString(cleanedExpected)
        if (normalizeText(simpInput) == normalizeText(simpExpected)) return true

        // 2. Check explicitly provided acceptable alternatives
        for (alt in acceptableAnswers) {
            if (normInput == normalizeText(alt)) return true
            val simpAlt = FractionHelper.simplifyFractionString(alt)
            if (normalizeText(simpInput) == normalizeText(simpAlt)) return true
        }

        // 3. Numeric & Fraction Evaluation
        val inputVal = parseNumericOrFraction(cleanedInput)
        val expectedVal = parseNumericOrFraction(cleanedExpected)

        if (inputVal != null && expectedVal != null) {
            // Tolerance comparison
            if (abs(inputVal - expectedVal) <= tolerance) {
                return true
            }
        }

        // Also check if any of the acceptable answers evaluate numerically
        if (inputVal != null) {
            for (alt in acceptableAnswers) {
                val altVal = parseNumericOrFraction(alt)
                if (altVal != null && abs(inputVal - altVal) <= tolerance) {
                    return true
                }
            }
        }

        return false
    }

    private fun normalizeText(text: String): String {
        return text.lowercase()
            .replace("°", "")
            .replace("degrees", "")
            .replace("degree", "")
            .replace("deg", "")
            .replace("cm", "")
            .replace("m", "")
            .replace("θ", "theta")
            .replace(" ", "")
    }

    /**
     * Parses a string representing either a decimal ("0.60"), whole number ("12"),
     * or fraction ("8/17", "3/5").
     */
    fun parseNumericOrFraction(text: String): Double? {
        val clean = text.trim()
            .replace("°", "")
            .replace("deg", "")
            .replace("cm", "")
            .replace(",", ".")
            .trim()

        if (clean.contains("/")) {
            val parts = clean.split("/")
            if (parts.size == 2) {
                val num = parts[0].trim().toDoubleOrNull()
                val den = parts[1].trim().toDoubleOrNull()
                if (num != null && den != null && den != 0.0) {
                    return num / den
                }
            }
        }

        return clean.toDoubleOrNull()
    }
}
