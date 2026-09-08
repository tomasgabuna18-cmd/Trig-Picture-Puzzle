package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.R
import com.example.trig.QuestionGenerator
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.model.PuzzleConfig
import com.example.model.TrigRatio
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class LocalPuzzleRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("offline_trig_puzzles_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PUZZLES_JSON = "key_stored_puzzles_json"

        fun copyImageToLocalStorage(context: Context, sourceUri: Uri): String? {
            return try {
                val dir = File(context.filesDir, "puzzle_photos")
                if (!dir.exists()) dir.mkdirs()
                val destFile = File(dir, "photo_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                destFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getPuzzles(): List<PuzzleConfig> {
        val jsonStr = prefs.getString(KEY_PUZZLES_JSON, null)
        if (jsonStr.isNullOrBlank()) {
            val defaults = createDefaultPuzzles()
            savePuzzles(defaults)
            return defaults
        }
        return try {
            val list = deserializePuzzles(jsonStr)
            if (list.isEmpty()) {
                val defaults = createDefaultPuzzles()
                savePuzzles(defaults)
                defaults
            } else {
                list
            }
        } catch (_: Exception) {
            val defaults = createDefaultPuzzles()
            savePuzzles(defaults)
            defaults
        }
    }

    fun savePuzzle(newPuzzle: PuzzleConfig): List<PuzzleConfig> {
        val current = getPuzzles().toMutableList()
        val index = current.indexOfFirst { it.id == newPuzzle.id }
        if (index >= 0) {
            current[index] = newPuzzle
        } else {
            current.add(newPuzzle)
        }
        savePuzzles(current)
        return current
    }

    fun updatePuzzleImage(puzzleId: String, customImageUri: String?, imageResId: Int?): List<PuzzleConfig> {
        val current = getPuzzles().map { p ->
            if (p.id == puzzleId) {
                p.copy(customImageUri = customImageUri, imageResId = imageResId)
            } else {
                p
            }
        }
        savePuzzles(current)
        return current
    }

    fun deletePuzzle(puzzleId: String): List<PuzzleConfig> {
        val current = getPuzzles().filterNot { it.id == puzzleId }
        val updated = if (current.isEmpty()) createDefaultPuzzles() else current
        savePuzzles(updated)
        return updated
    }

    private fun savePuzzles(puzzles: List<PuzzleConfig>) {
        val array = JSONArray()
        for (p in puzzles) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("title", p.title)
                put("destinationName", p.destinationName)
                put("description", p.description)
                put("gridSize", p.gridSize)
                put("imageResId", p.imageResId ?: -1)
                put("customImageUri", p.customImageUri ?: "")
                put("gameMode", p.gameMode.name)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PUZZLES_JSON, array.toString()).apply()
    }

    private fun deserializePuzzles(jsonStr: String): List<PuzzleConfig> {
        val array = JSONArray(jsonStr)
        val list = mutableListOf<PuzzleConfig>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val resIdInt = obj.optInt("imageResId", -1)
            val resId = if (resIdInt != -1) resIdInt else null
            val customUri = obj.optString("customImageUri", "").takeIf { it.isNotBlank() }
            val gridSize = obj.optInt("gridSize", 4)
            val modeStr = obj.optString("gameMode", GameMode.CLASSIC.name)
            val mode = try { GameMode.valueOf(modeStr) } catch (_: Exception) { GameMode.CLASSIC }

            val totalCount = gridSize * gridSize
            val questions = QuestionGenerator.generateQuestionSet(totalCount, TrigRatio.entries, Difficulty.AVERAGE)

            list.add(
                PuzzleConfig(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    destinationName = obj.getString("destinationName"),
                    description = obj.getString("description"),
                    imageResId = resId,
                    customImageUri = customUri,
                    gridSize = gridSize,
                    gameMode = mode,
                    questions = questions
                )
            )
        }
        return list
    }

    private fun createDefaultPuzzles(): List<PuzzleConfig> {
        val romblonQuestions = QuestionGenerator.createSampleRomblonQuestions()
        val romblonPuzzle = PuzzleConfig(
            id = "sample_romblon",
            title = "Discover the Hidden Destination",
            destinationName = "Cresta de Gallo Island, Romblon",
            description = "Uncover the pristine white sandbars and turquoise waters of Romblon, Philippines by solving 16 trigonometry problems across all 6 ratios.",
            imageResId = R.drawable.img_destination_romblon,
            gridSize = 4,
            gameMode = GameMode.CLASSIC,
            questions = romblonQuestions
        )

        val palawanQuestions = QuestionGenerator.generateQuestionSet(16, TrigRatio.entries, Difficulty.AVERAGE)
        val palawanPuzzle = PuzzleConfig(
            id = "sample_palawan",
            title = "The Emerald Lagoons of El Nido",
            destinationName = "El Nido, Palawan",
            description = "Explore dramatic limestone karst cliffs and azure waters while solving intermediate trigonometric ratio equations.",
            imageResId = R.drawable.img_destination_palawan,
            gridSize = 4,
            gameMode = GameMode.CLASSIC,
            questions = palawanQuestions
        )

        val mayonQuestions = QuestionGenerator.generateQuestionSet(16, TrigRatio.entries, Difficulty.AVERAGE)
        val mayonPuzzle = PuzzleConfig(
            id = "sample_mayon",
            title = "Mayon Volcano Wonders",
            destinationName = "Albay, Bicol",
            description = "Solve right triangle trigonometric ratios to uncover the world-renowned symmetrical cone volcano surrounded by verdant fields.",
            imageResId = R.drawable.img_destination_mayon,
            gridSize = 4,
            gameMode = GameMode.CLASSIC,
            questions = mayonQuestions
        )

        val banaueQuestions = QuestionGenerator.generateQuestionSet(16, TrigRatio.entries, Difficulty.AVERAGE)
        val banauePuzzle = PuzzleConfig(
            id = "sample_banaue",
            title = "Banaue Rice Terraces Heritage",
            destinationName = "Ifugao, Cordillera",
            description = "Calculate sine, cosine, and tangent step heights on ancient 2,000-year-old emerald mountain terraces carved into the slopes.",
            imageResId = R.drawable.img_destination_banaue,
            gridSize = 4,
            gameMode = GameMode.CLASSIC,
            questions = banaueQuestions
        )

        return listOf(romblonPuzzle, palawanPuzzle, mayonPuzzle, banauePuzzle)
    }
}
