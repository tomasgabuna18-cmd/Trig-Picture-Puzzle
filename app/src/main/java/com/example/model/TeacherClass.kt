package com.example.model

import java.util.UUID

data class TeacherClass(
    val id: String = UUID.randomUUID().toString(),
    val className: String,
    val subject: String = "Mathematics 9 - Trigonometry",
    val section: String = "Section A",
    val classCode: String = generateClassCode(),
    val studentRoster: List<String> = emptyList(),
    val assignedPuzzleTitle: String = "Discover Romblon: Marble Capital",
    val targetAccuracyGoal: Int = 85,
    val schedule: String = "MWF 9:00 AM",
    val notes: String = "",
    val isOfflineReady: Boolean = true,
    val createdAtEpochMs: Long = System.currentTimeMillis()
) {
    companion object {
        fun generateClassCode(): String {
            val randomNum = (1000..9999).random()
            return "TRIG-$randomNum"
        }
    }
}
