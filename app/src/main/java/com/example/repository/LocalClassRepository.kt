package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.TeacherClass
import org.json.JSONArray
import org.json.JSONObject

class LocalClassRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("offline_teacher_classes_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CLASSES_JSON = "key_teacher_classes_json"
    }

    fun getClasses(): List<TeacherClass> {
        val jsonStr = prefs.getString(KEY_CLASSES_JSON, null)
        if (jsonStr.isNullOrBlank()) {
            val defaults = createDefaultClasses()
            saveClasses(defaults)
            return defaults
        }
        return try {
            deserializeClasses(jsonStr)
        } catch (_: Exception) {
            val defaults = createDefaultClasses()
            saveClasses(defaults)
            defaults
        }
    }

    fun saveClass(newClass: TeacherClass): List<TeacherClass> {
        val current = getClasses().toMutableList()
        val existingIndex = current.indexOfFirst { it.id == newClass.id }
        if (existingIndex >= 0) {
            current[existingIndex] = newClass
        } else {
            current.add(0, newClass)
        }
        saveClasses(current)
        return current
    }

    fun deleteClass(classId: String): List<TeacherClass> {
        val updated = getClasses().filterNot { it.id == classId }
        saveClasses(updated)
        return updated
    }

    fun addStudent(classId: String, studentName: String): List<TeacherClass> {
        val trimmed = studentName.trim()
        if (trimmed.isBlank()) return getClasses()
        val current = getClasses().map { c ->
            if (c.id == classId && !c.studentRoster.contains(trimmed)) {
                c.copy(studentRoster = c.studentRoster + trimmed)
            } else {
                c
            }
        }
        saveClasses(current)
        return current
    }

    fun removeStudent(classId: String, studentName: String): List<TeacherClass> {
        val current = getClasses().map { c ->
            if (c.id == classId) {
                c.copy(studentRoster = c.studentRoster - studentName)
            } else {
                c
            }
        }
        saveClasses(current)
        return current
    }

    fun updateClassPuzzle(classId: String, puzzleTitle: String): List<TeacherClass> {
        val current = getClasses().map { c ->
            if (c.id == classId) {
                c.copy(assignedPuzzleTitle = puzzleTitle)
            } else {
                c
            }
        }
        saveClasses(current)
        return current
    }

    private fun saveClasses(classes: List<TeacherClass>) {
        val array = JSONArray()
        for (c in classes) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("className", c.className)
                put("subject", c.subject)
                put("section", c.section)
                put("classCode", c.classCode)
                put("assignedPuzzleTitle", c.assignedPuzzleTitle)
                put("targetAccuracyGoal", c.targetAccuracyGoal)
                put("schedule", c.schedule)
                put("notes", c.notes)
                put("isOfflineReady", c.isOfflineReady)
                put("createdAtEpochMs", c.createdAtEpochMs)
                val rosterArray = JSONArray()
                c.studentRoster.forEach { rosterArray.put(it) }
                put("studentRoster", rosterArray)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CLASSES_JSON, array.toString()).apply()
    }

    private fun deserializeClasses(jsonStr: String): List<TeacherClass> {
        val array = JSONArray(jsonStr)
        val result = mutableListOf<TeacherClass>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val roster = mutableListOf<String>()
            val rosterArray = obj.optJSONArray("studentRoster")
            if (rosterArray != null) {
                for (j in 0 until rosterArray.length()) {
                    roster.add(rosterArray.getString(j))
                }
            }
            result.add(
                TeacherClass(
                    id = obj.getString("id"),
                    className = obj.getString("className"),
                    subject = obj.optString("subject", "Mathematics 9 - Trigonometry"),
                    section = obj.optString("section", "Section A"),
                    classCode = obj.optString("classCode", TeacherClass.generateClassCode()),
                    studentRoster = roster,
                    assignedPuzzleTitle = obj.optString("assignedPuzzleTitle", "Discover Romblon: Marble Capital"),
                    targetAccuracyGoal = obj.optInt("targetAccuracyGoal", 85),
                    schedule = obj.optString("schedule", "MWF 9:00 AM"),
                    notes = obj.optString("notes", ""),
                    isOfflineReady = obj.optBoolean("isOfflineReady", true),
                    createdAtEpochMs = obj.optLong("createdAtEpochMs", System.currentTimeMillis())
                )
            )
        }
        return result
    }

    private fun createDefaultClasses(): List<TeacherClass> {
        return listOf(
            TeacherClass(
                className = "Grade 9 - Section Newton",
                subject = "Mathematics 9",
                section = "Newton",
                classCode = "TRIG-9021",
                studentRoster = listOf("Juan Dela Cruz", "Maria Santos", "David Lee", "Ana Ramos", "Miguel Cruz"),
                assignedPuzzleTitle = "Discover Romblon: Marble Capital",
                targetAccuracyGoal = 85,
                schedule = "MWF 8:00 AM - 9:00 AM",
                notes = "Focus on Sine and Cosine acute angle calculations",
                isOfflineReady = true
            ),
            TeacherClass(
                className = "Grade 9 - Section Euler",
                subject = "Mathematics 9",
                section = "Euler",
                classCode = "TRIG-4172",
                studentRoster = listOf("Clara Reyes", "Paolo Garcia", "Sarah Tan", "Joshua Santos"),
                assignedPuzzleTitle = "Palawan: Limestone Reef Adventure",
                targetAccuracyGoal = 80,
                schedule = "TTh 10:00 AM - 11:30 AM",
                notes = "Reciprocal ratios (csc, sec, cot) exploration",
                isOfflineReady = true
            )
        )
    }
}
