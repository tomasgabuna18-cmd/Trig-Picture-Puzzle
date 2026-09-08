package com.example

import com.example.jigsaw.JigsawGridGenerator
import com.example.model.EdgeType
import com.example.model.TrigRatio
import com.example.trig.AnswerValidator
import com.example.trig.QuestionGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TrigPuzzleUnitTest {

    @Test
    fun testAnswerValidator_fractionsAndDecimals() {
        // 3/5 is mathematically equal to 0.6 and 6/10
        assertTrue(AnswerValidator.isCorrect("3/5", "3/5"))
        assertTrue(AnswerValidator.isCorrect("0.6", "3/5"))
        assertTrue(AnswerValidator.isCorrect("0.60", "3/5"))
        assertTrue(AnswerValidator.isCorrect("6/10", "3/5"))

        // Simplified fraction checks
        assertTrue(AnswerValidator.isCorrect("6/10", "3/5"))
        assertTrue(AnswerValidator.isCorrect("3/5", "6/10"))
        assertTrue(AnswerValidator.isCorrect("15/12", "5/4"))

        // Acceptable alternatives
        assertTrue(AnswerValidator.isCorrect("0.88", "15/17", listOf("0.88", "0.882")))
        assertFalse(AnswerValidator.isCorrect("0.5", "3/5"))
    }

    @Test
    fun testFractionHelper_simplification() {
        // Test GCD and fraction simplification
        org.junit.Assert.assertEquals("3/5", com.example.trig.FractionHelper.simplify(6, 10))
        org.junit.Assert.assertEquals("5/4", com.example.trig.FractionHelper.simplify(15, 12))
        org.junit.Assert.assertEquals("12/5", com.example.trig.FractionHelper.simplify(24, 10))
        org.junit.Assert.assertEquals("3/4", com.example.trig.FractionHelper.simplify(6, 8))
        org.junit.Assert.assertEquals("5/1", com.example.trig.FractionHelper.simplify(10, 2))
        org.junit.Assert.assertEquals("5", com.example.trig.FractionHelper.simplify(10, 2, asIntegerWhenWhole = true))

        // Test string fraction simplification
        org.junit.Assert.assertEquals("3/5", com.example.trig.FractionHelper.simplifyFractionString("6/10"))
        org.junit.Assert.assertEquals("5/4", com.example.trig.FractionHelper.simplifyFractionString("15/12"))
        org.junit.Assert.assertEquals("cos θ", com.example.trig.FractionHelper.simplifyFractionString("cos θ"))
        org.junit.Assert.assertEquals("45°", com.example.trig.FractionHelper.simplifyFractionString("45°"))

        // Test option list simplification and deduplication
        val rawOptions = listOf("6/10", "3/5", "8/10", "15/20")
        val simplified = com.example.trig.FractionHelper.simplifyOptions(rawOptions)
        org.junit.Assert.assertEquals(listOf("3/5", "4/5"), simplified)
    }

    @Test
    fun testAnswerValidator_anglesAndDegrees() {
        assertTrue(AnswerValidator.isCorrect("30°", "30"))
        assertTrue(AnswerValidator.isCorrect("30 deg", "30"))
        assertTrue(AnswerValidator.isCorrect("30", "30°"))
        assertTrue(AnswerValidator.isCorrect("45°", "45", listOf("45 deg")))
    }

    @Test
    fun testAnswerValidator_multipleChoiceAndRatios() {
        assertTrue(AnswerValidator.isCorrect("cos θ", "cos θ"))
        assertTrue(AnswerValidator.isCorrect("cos", "cos θ", listOf("cosine", "cos")))
        assertTrue(AnswerValidator.isCorrect("sin", "sin θ", listOf("sine", "sin")))
    }

    @Test
    fun testQuestionGenerator_coversAllSixRatios() {
        val questions = QuestionGenerator.createSampleRomblonQuestions()
        assertEquals(16, questions.size)

        val ratiosPresent = questions.map { it.ratio }.toSet()
        assertEquals(6, ratiosPresent.size)

        assertTrue(ratiosPresent.contains(TrigRatio.SINE))
        assertTrue(ratiosPresent.contains(TrigRatio.COSINE))
        assertTrue(ratiosPresent.contains(TrigRatio.TANGENT))
        assertTrue(ratiosPresent.contains(TrigRatio.COSECANT))
        assertTrue(ratiosPresent.contains(TrigRatio.SECANT))
        assertTrue(ratiosPresent.contains(TrigRatio.COTANGENT))

        // Verify all 16 questions have multiple choice options containing the correct answer
        assertTrue(questions.all { q ->
            val opts = q.options
            opts != null && opts.size >= 4 && opts.contains(q.correctAnswer)
        })
    }

    @Test
    fun testJigsawGridGenerator_interlockingEdgesMatch() {
        val gridSize = 4
        val generator = JigsawGridGenerator(gridSize)

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val pieceEdges = generator.getEdgesForPiece(r, c)

                // Outer border edges must be FLAT
                if (r == 0) assertEquals(EdgeType.FLAT, pieceEdges.top)
                if (r == gridSize - 1) assertEquals(EdgeType.FLAT, pieceEdges.bottom)
                if (c == 0) assertEquals(EdgeType.FLAT, pieceEdges.left)
                if (c == gridSize - 1) assertEquals(EdgeType.FLAT, pieceEdges.right)

                // Check right neighbor interlocking
                if (c < gridSize - 1) {
                    val rightNeighborEdges = generator.getEdgesForPiece(r, c + 1)
                    if (pieceEdges.right == EdgeType.TAB) {
                        assertEquals(EdgeType.SOCKET, rightNeighborEdges.left)
                    } else if (pieceEdges.right == EdgeType.SOCKET) {
                        assertEquals(EdgeType.TAB, rightNeighborEdges.left)
                    }
                }

                // Check bottom neighbor interlocking
                if (r < gridSize - 1) {
                    val bottomNeighborEdges = generator.getEdgesForPiece(r + 1, c)
                    if (pieceEdges.bottom == EdgeType.TAB) {
                        assertEquals(EdgeType.SOCKET, bottomNeighborEdges.top)
                    } else if (pieceEdges.bottom == EdgeType.SOCKET) {
                        assertEquals(EdgeType.TAB, bottomNeighborEdges.top)
                    }
                }
            }
        }
    }

    @Test
    fun testTrigQuestionRepository_fallbackGeneratesValidQuestions() {
        val repo = com.example.repository.GeminiTrigQuestionRepository()
        kotlinx.coroutines.runBlocking {
            val result = repo.generateQuestions(
                difficulty = com.example.model.Difficulty.AVERAGE,
                category = com.example.model.QuestionCategory.FIND_RATIO,
                count = 6,
                targetRatios = TrigRatio.entries,
                fallbackOnFailure = true
            )

            assertTrue(result.isSuccess)
            val questions = result.getOrNull()
            org.junit.Assert.assertNotNull(questions)
            assertEquals(6, questions!!.size)
            assertTrue(questions.all { it.problemText.isNotBlank() })
            assertTrue(questions.all { it.correctAnswer.isNotBlank() })
            assertTrue(questions.all { it.hint1.isNotBlank() })
            assertTrue(questions.all { it.hint2.isNotBlank() })
            assertTrue(questions.all { q ->
                val opts = q.options
                opts != null && opts.size >= 4 && opts.contains(q.correctAnswer)
            })
        }
    }

    @Test
    fun testOfflineTeacherClass_creationAndRosterManagement() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val classRepo = com.example.repository.LocalClassRepository(context)

        // Verify initial default offline classes exist
        val initialClasses = classRepo.getClasses()
        assertTrue(initialClasses.isNotEmpty())
        assertTrue(initialClasses.any { it.isOfflineReady })

        // Create a new offline class
        val newClass = com.example.model.TeacherClass(
            className = "Grade 9 - Section Archimedes",
            subject = "Trigonometry & Right Triangles",
            section = "Archimedes",
            schedule = "MWF 1:00 PM",
            assignedPuzzleTitle = "Discover Romblon: Marble Capital",
            targetAccuracyGoal = 90,
            notes = "Offline Unit 3 trigonometry assessment",
            studentRoster = listOf("Maria Clara", "Crisostomo Ibarra")
        )

        val updatedAfterSave = classRepo.saveClass(newClass)
        val saved = updatedAfterSave.find { it.id == newClass.id }
        org.junit.Assert.assertNotNull(saved)
        assertEquals("Grade 9 - Section Archimedes", saved!!.className)
        assertTrue(saved.classCode.startsWith("TRIG-"))
        assertEquals(2, saved.studentRoster.size)

        // Add a student offline
        val afterAddStudent = classRepo.addStudent(newClass.id, "Elias Cruz")
        val withNewStudent = afterAddStudent.find { it.id == newClass.id }
        org.junit.Assert.assertNotNull(withNewStudent)
        assertTrue(withNewStudent!!.studentRoster.contains("Elias Cruz"))
        assertEquals(3, withNewStudent.studentRoster.size)

        // Remove a student offline
        val afterRemoveStudent = classRepo.removeStudent(newClass.id, "Maria Clara")
        val withRemoved = afterRemoveStudent.find { it.id == newClass.id }
        assertFalse(withRemoved!!.studentRoster.contains("Maria Clara"))
        assertEquals(2, withRemoved.studentRoster.size)

        // Delete class
        val afterDelete = classRepo.deleteClass(newClass.id)
        assertFalse(afterDelete.any { it.id == newClass.id })
    }

    @Test
    fun testOfflinePuzzleRepository_andPhotoManagement() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val puzzleRepo = com.example.repository.LocalPuzzleRepository(context)

        // Verify initial default puzzles
        val initialPuzzles = puzzleRepo.getPuzzles()
        assertTrue(initialPuzzles.isNotEmpty())
        assertTrue(initialPuzzles.any { it.destinationName.contains("Romblon") })
        assertTrue(initialPuzzles.any { it.destinationName.contains("Palawan") })
        assertTrue(initialPuzzles.any { it.destinationName.contains("Mayon") || it.destinationName.contains("Albay") })
        assertTrue(initialPuzzles.any { it.destinationName.contains("Banaue") || it.destinationName.contains("Ifugao") })

        // Create a custom puzzle with uploaded photo URI
        val customPuzzle = com.example.model.PuzzleConfig(
            id = "custom_test_puzzle_1",
            title = "Coron Twin Lagoons",
            destinationName = "Coron, Palawan",
            description = "Custom uploaded photo puzzle for trigonometry students",
            gridSize = 3,
            customImageUri = "/data/user/0/com.example/files/puzzle_photos/photo_test.jpg",
            imageResId = null
        )

        val updatedPuzzles = puzzleRepo.savePuzzle(customPuzzle)
        val saved = updatedPuzzles.find { it.id == "custom_test_puzzle_1" }
        org.junit.Assert.assertNotNull(saved)
        assertEquals("/data/user/0/com.example/files/puzzle_photos/photo_test.jpg", saved!!.customImageUri)
        assertEquals(null, saved.imageResId)

        // Update photo to a landmark preset
        val afterPresetChange = puzzleRepo.updatePuzzleImage("custom_test_puzzle_1", customImageUri = null, imageResId = R.drawable.img_destination_palawan)
        val changedPreset = afterPresetChange.find { it.id == "custom_test_puzzle_1" }
        org.junit.Assert.assertNotNull(changedPreset)
        assertEquals(null, changedPreset!!.customImageUri)
        assertEquals(R.drawable.img_destination_palawan, changedPreset.imageResId)

        // Update photo to a new custom image
        val afterNewPhoto = puzzleRepo.updatePuzzleImage("custom_test_puzzle_1", customImageUri = "/data/user/0/com.example/files/puzzle_photos/new_photo.jpg", imageResId = null)
        val changedPhoto = afterNewPhoto.find { it.id == "custom_test_puzzle_1" }
        org.junit.Assert.assertNotNull(changedPhoto)
        assertEquals("/data/user/0/com.example/files/puzzle_photos/new_photo.jpg", changedPhoto!!.customImageUri)

        // Delete custom puzzle
        val afterDelete = puzzleRepo.deletePuzzle("custom_test_puzzle_1")
        assertFalse(afterDelete.any { it.id == "custom_test_puzzle_1" })
    }
}
