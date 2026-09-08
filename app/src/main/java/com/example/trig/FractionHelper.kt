package com.example.trig

import kotlin.math.abs

/**
 * Utility to simplify and format mathematical fractions and trigonometric ratio strings.
 * Ensures answers and choices like "6/10" are reduced to simplified form like "3/5",
 * or "2/4" to "1/2", etc.
 */
object FractionHelper {

    /**
     * Returns the greatest common divisor of two non-negative integers.
     */
    fun gcd(a: Long, b: Long): Long {
        var x = abs(a)
        var y = abs(b)
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return if (x == 0L) 1L else x
    }

    /**
     * Simplifies a fraction given numerator and denominator as integers.
     * E.g. simplify(6, 10) -> "3/5"
     * simplify(15, 12) -> "5/4"
     * simplify(10, 2) -> "5" (if asIntegerWhenWhole = true) or "5/1"
     */
    fun simplify(numerator: Int, denominator: Int, asIntegerWhenWhole: Boolean = false): String {
        if (denominator == 0) return "$numerator/0"
        val sign = if ((numerator < 0) xor (denominator < 0)) -1 else 1
        val num = abs(numerator.toLong())
        val den = abs(denominator.toLong())
        val common = gcd(num, den)

        val simpNum = (num / common) * sign
        val simpDen = den / common

        return if (asIntegerWhenWhole && simpDen == 1L) {
            simpNum.toString()
        } else {
            "$simpNum/$simpDen"
        }
    }

    /**
     * Simplifies any fraction string if it is of the form "a/b" with integers a and b.
     * If the string is not a simple fraction (e.g. "30°", "cos θ", "12", "0.6"),
     * it returns the trimmed original string.
     */
    fun simplifyFractionString(text: String, asIntegerWhenWhole: Boolean = false): String {
        val clean = text.trim()
        if (!clean.contains("/")) return clean

        val parts = clean.split("/")
        if (parts.size == 2) {
            val num = parts[0].trim().toIntOrNull()
            val den = parts[1].trim().toIntOrNull()
            if (num != null && den != null && den != 0) {
                return simplify(num, den, asIntegerWhenWhole)
            }
        }
        return clean
    }

    /**
     * Simplifies a list of answer choices/options.
     * Eliminates duplicates that may arise after reduction, and maintains 4 distinct choices.
     */
    fun simplifyOptions(options: List<String>): List<String> {
        val simplified = mutableListOf<String>()
        val seen = mutableSetOf<String>()

        for (opt in options) {
            val sim = simplifyFractionString(opt)
            if (seen.add(sim)) {
                simplified.add(sim)
            }
        }
        return simplified
    }
}
