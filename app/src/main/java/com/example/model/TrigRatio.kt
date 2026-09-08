package com.example.model

enum class TrigRatio(
    val displayName: String,
    val abbreviation: String,
    val formulaText: String,
    val mnemonic: String,
    val definition: String
) {
    SINE(
        displayName = "Sine",
        abbreviation = "sin",
        formulaText = "sin θ = Opposite / Hypotenuse",
        mnemonic = "SOH",
        definition = "Ratio of the length of the side opposite θ to the hypotenuse."
    ),
    COSINE(
        displayName = "Cosine",
        abbreviation = "cos",
        formulaText = "cos θ = Adjacent / Hypotenuse",
        mnemonic = "CAH",
        definition = "Ratio of the length of the side adjacent to θ to the hypotenuse."
    ),
    TANGENT(
        displayName = "Tangent",
        abbreviation = "tan",
        formulaText = "tan θ = Opposite / Adjacent",
        mnemonic = "TOA",
        definition = "Ratio of the length of the side opposite θ to the adjacent side."
    ),
    COSECANT(
        displayName = "Cosecant",
        abbreviation = "csc",
        formulaText = "csc θ = Hypotenuse / Opposite = 1 / sin θ",
        mnemonic = "Reciprocal of Sine",
        definition = "Reciprocal of sine; ratio of hypotenuse to opposite side."
    ),
    SECANT(
        displayName = "Secant",
        abbreviation = "sec",
        formulaText = "sec θ = Hypotenuse / Adjacent = 1 / cos θ",
        mnemonic = "Reciprocal of Cosine",
        definition = "Reciprocal of cosine; ratio of hypotenuse to adjacent side."
    ),
    COTANGENT(
        displayName = "Cotangent",
        abbreviation = "cot",
        formulaText = "cot θ = Adjacent / Opposite = 1 / tan θ",
        mnemonic = "Reciprocal of Tangent",
        definition = "Reciprocal of tangent; ratio of adjacent to opposite side."
    );

    val reciprocalOf: String get() = mnemonic

    companion object {
        fun fromAbbreviation(abbr: String): TrigRatio? {
            return entries.find { it.abbreviation.equals(abbr.trim(), ignoreCase = true) }
        }
    }
}

enum class QuestionCategory(val title: String) {
    IDENTIFY_RATIO("Identify the Ratio"),
    FIND_RATIO("Find the Ratio"),
    FIND_SIDE("Find Unknown Side"),
    FIND_ANGLE("Find the Angle"),
    RECIPROCAL("Reciprocal Ratios"),
    MIXED_CHALLENGE("Comprehensive Trigonometry")
}

enum class Difficulty(val label: String, val basePoints: Int) {
    EASY("Easy", 100),
    AVERAGE("Average", 125),
    DIFFICULT("Difficult", 150);

    val displayName: String get() = label
}

enum class GameMode(val title: String, val description: String) {
    CLASSIC("Classic", "Solve questions at your own pace to reveal the hidden picture."),
    TIMED("Timed", "Race against the clock to complete the puzzle!"),
    RELAXED("Relaxed", "No timer, no score penalties, and unlimited hints."),
    CHALLENGE("Challenge", "Questions escalate from easy to difficult."),
    SIX_RATIO("Six-Ratio Challenge", "Balanced questions covering all 6 trigonometric ratios."),
    TWO_PLAYER("Two-Player Hotseat", "Take turns solving problems to claim puzzle pieces!"),
    TEAM_MODE("Team Collaborative", "Work together as a class to solve the puzzle.");

    val displayName: String get() = title
}
