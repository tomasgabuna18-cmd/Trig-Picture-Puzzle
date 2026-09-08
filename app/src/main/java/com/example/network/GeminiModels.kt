package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentRequest(
    @param:Json(name = "contents") val contents: List<GeminiContent>,
    @param:Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @param:Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @param:Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @param:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @param:Json(name = "responseMimeType") val responseMimeType: String? = "application/json",
    @param:Json(name = "temperature") val temperature: Float? = 0.4f,
    @param:Json(name = "topP") val topP: Float? = 0.95f,
    @param:Json(name = "topK") val topK: Int? = 40
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentResponse(
    @param:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @param:Json(name = "content") val content: GeminiContent? = null,
    @param:Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiQuestionJsonDto(
    @param:Json(name = "id") val id: String? = null,
    @param:Json(name = "ratio") val ratio: String,
    @param:Json(name = "category") val category: String? = null,
    @param:Json(name = "difficulty") val difficulty: String? = null,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "problemText") val problemText: String,
    @param:Json(name = "opposite") val opposite: Double? = null,
    @param:Json(name = "adjacent") val adjacent: Double? = null,
    @param:Json(name = "hypotenuse") val hypotenuse: Double? = null,
    @param:Json(name = "thetaDegrees") val thetaDegrees: Double? = null,
    @param:Json(name = "unknownSide") val unknownSide: String? = null,
    @param:Json(name = "orientation") val orientation: String? = null,
    @param:Json(name = "options") val options: List<String>? = null,
    @param:Json(name = "correctAnswer") val correctAnswer: String,
    @param:Json(name = "acceptableAnswers") val acceptableAnswers: List<String>? = null,
    @param:Json(name = "hint1") val hint1: String,
    @param:Json(name = "hint2") val hint2: String,
    @param:Json(name = "explanation") val explanation: String,
    @param:Json(name = "points") val points: Int? = null
)
