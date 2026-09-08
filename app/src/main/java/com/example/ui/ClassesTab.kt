package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PuzzleConfig
import com.example.model.TeacherClass

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassesTab(
    classes: List<TeacherClass>,
    availablePuzzles: List<PuzzleConfig>,
    onCreateClass: (
        className: String,
        subject: String,
        section: String,
        schedule: String,
        assignedPuzzle: String,
        targetGoal: Int,
        notes: String,
        students: List<String>
    ) -> Unit,
    onDeleteClass: (classId: String) -> Unit,
    onAddStudent: (classId: String, studentName: String) -> Unit,
    onRemoveStudent: (classId: String, studentName: String) -> Unit,
    onLaunchClassPuzzle: (teacherClass: TeacherClass) -> Unit
) {
    var showCreateForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Offline Classroom Header Banner ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Offline Classroom Management",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Create classes & manage student rosters locally",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Offline Badge
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "100% Offline Ready",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${classes.size} Active Offline Class${if (classes.size == 1) "" else "es"}",
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = { showCreateForm = !showCreateForm },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCreateForm) Color(0xFF334155) else Color(0xFF38BDF8),
                            contentColor = if (showCreateForm) Color.White else Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("create_class_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (showCreateForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showCreateForm) "Close Form" else "+ Create Class",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // --- Create Class Form ---
        AnimatedVisibility(visible = showCreateForm) {
            CreateClassCard(
                availablePuzzles = availablePuzzles,
                onCancel = { showCreateForm = false },
                onCreate = { name, subj, sec, sched, puzzle, goal, notes, students ->
                    onCreateClass(name, subj, sec, sched, puzzle, goal, notes, students)
                    showCreateForm = false
                }
            )
        }

        // --- List of Classes ---
        if (classes.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Classes Created Yet",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Create your first class offline to organize rosters, set accuracy targets, and assign puzzles to students.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCreateForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Create Class Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            classes.forEach { classItem ->
                ClassItemCard(
                    teacherClass = classItem,
                    onDelete = { onDeleteClass(classItem.id) },
                    onAddStudent = { studentName -> onAddStudent(classItem.id, studentName) },
                    onRemoveStudent = { studentName -> onRemoveStudent(classItem.id, studentName) },
                    onLaunchPuzzle = { onLaunchClassPuzzle(classItem) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateClassCard(
    availablePuzzles: List<PuzzleConfig>,
    onCancel: () -> Unit,
    onCreate: (
        className: String,
        subject: String,
        section: String,
        schedule: String,
        assignedPuzzle: String,
        targetGoal: Int,
        notes: String,
        students: List<String>
    ) -> Unit
) {
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Mathematics 9 - Trigonometry") }
    var schedule by remember { mutableStateOf("MWF 9:00 AM - 10:00 AM") }
    var targetGoal by remember { mutableIntStateOf(85) }
    var notes by remember { mutableStateOf("") }
    var studentNamesInput by remember { mutableStateOf("") }
    var selectedPuzzleTitle by remember {
        mutableStateOf(availablePuzzles.firstOrNull()?.title ?: "Discover Romblon: Marble Capital")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("create_class_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Offline Class Details",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontSize = 16.sp
                )
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Auto-generates Offline Code",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Class Name
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Class Name *") },
                placeholder = { Text("e.g. Grade 9 - Newton") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("class_name_input"),
                colors = defaultTextFieldColors()
            )

            // Section & Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("Section / Room") },
                    placeholder = { Text("e.g. Section A") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("class_section_input"),
                    colors = defaultTextFieldColors()
                )
                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Schedule") },
                    placeholder = { Text("e.g. MWF 9:00 AM") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("class_schedule_input"),
                    colors = defaultTextFieldColors()
                )
            }

            // Subject
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )

            // Assigned Puzzle Selector
            Text(
                text = "Assign Puzzle Destination:",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                availablePuzzles.forEach { puzzle ->
                    val isSelected = selectedPuzzleTitle == puzzle.title
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPuzzleTitle = puzzle.title },
                        label = { Text(puzzle.destinationName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF38BDF8),
                            selectedLabelColor = Color(0xFF0F172A),
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color.White
                        )
                    )
                }
            }

            // Target Accuracy Goal
            Text(
                text = "Target Accuracy Goal: $targetGoal%",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(75, 80, 85, 90, 95).forEach { goal ->
                    FilterChip(
                        selected = targetGoal == goal,
                        onClick = { targetGoal = goal },
                        label = { Text("$goal%") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981),
                            selectedLabelColor = Color(0xFF0F172A),
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color.White
                        )
                    )
                }
            }

            // Initial Student Roster input
            OutlinedTextField(
                value = studentNamesInput,
                onValueChange = { studentNamesInput = it },
                label = { Text("Initial Student Names (Optional)") },
                placeholder = { Text("Separate by commas (e.g. Juan Dela Cruz, Maria Santos, David Lee)") },
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("initial_students_input"),
                colors = defaultTextFieldColors()
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Teacher Notes (Optional)") },
                placeholder = { Text("e.g. Unit 4 Right Triangles Assessment") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = defaultTextFieldColors()
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF94A3B8))
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (className.isNotBlank()) {
                            val parsedStudents = studentNamesInput
                                .split(",", "\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            onCreate(
                                className,
                                subject,
                                section,
                                schedule,
                                selectedPuzzleTitle,
                                targetGoal,
                                notes,
                                parsedStudents
                            )
                        }
                    },
                    enabled = className.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_create_class_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Class Offline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassItemCard(
    teacherClass: TeacherClass,
    onDelete: () -> Unit,
    onAddStudent: (studentName: String) -> Unit,
    onRemoveStudent: (studentName: String) -> Unit,
    onLaunchPuzzle: () -> Unit
) {
    var isRosterExpanded by remember { mutableStateOf(false) }
    var newStudentName by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .testTag("class_card_${teacherClass.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Class Name, Code, and Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = teacherClass.className,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        if (teacherClass.section.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = teacherClass.section,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = teacherClass.subject,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                // Offline Join Code Badge
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFLINE CODE",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = teacherClass.classCode,
                            color = Color(0xFF38BDF8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Class",
                        tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info Chips Row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "📍 ${teacherClass.assignedPuzzleTitle}",
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🎯 Target: ${teacherClass.targetAccuracyGoal}%",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (teacherClass.schedule.isNotBlank()) {
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🕒 ${teacherClass.schedule}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (teacherClass.notes.isNotBlank()) {
                Text(
                    text = "📝 ${teacherClass.notes}",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            HorizontalDivider(color = Color(0xFF334155))

            // Student Roster Header & Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isRosterExpanded = !isRosterExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Class Roster (${teacherClass.studentRoster.size} Students)",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = if (isRosterExpanded) "Hide Roster ▲" else "View Roster ▼",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Expandable Roster List & Quick Add
            AnimatedVisibility(visible = isRosterExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (teacherClass.studentRoster.isEmpty()) {
                        Text(
                            text = "No students enrolled yet. Add students below for offline tracking.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            teacherClass.studentRoster.forEach { student ->
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = student,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove $student",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { onRemoveStudent(student) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Inline Quick Add Student
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newStudentName,
                            onValueChange = { newStudentName = it },
                            placeholder = { Text("Add student name...", color = Color(0xFF64748B), fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("quick_add_student_input_${teacherClass.id}"),
                            colors = defaultTextFieldColors()
                        )

                        Button(
                            onClick = {
                                if (newStudentName.isNotBlank()) {
                                    onAddStudent(newStudentName.trim())
                                    newStudentName = ""
                                }
                            },
                            enabled = newStudentName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("quick_add_student_button_${teacherClass.id}")
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Launch Puzzle for this Class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onLaunchPuzzle,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8), contentColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("launch_class_puzzle_button_${teacherClass.id}")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Launch Assigned Puzzle", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFF38BDF8),
    unfocusedBorderColor = Color(0xFF334155),
    cursorColor = Color(0xFF38BDF8),
    focusedLabelColor = Color(0xFF38BDF8),
    unfocusedLabelColor = Color(0xFF94A3B8)
)
