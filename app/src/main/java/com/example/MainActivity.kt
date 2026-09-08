package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.StudentGameScreen
import com.example.ui.TeacherDashboardScreen
import com.example.ui.TopicsReferenceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AppBottomNavigation(
                            activeTab = uiState.activeTab,
                            onTabSelected = { tab -> viewModel.setAppTab(tab) }
                        )
                    }
                ) { innerPadding ->
                    when (uiState.activeTab) {
                        AppTab.STUDENT_GAME -> {
                            StudentGameScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppTab.TEACHER_DASHBOARD -> {
                            TeacherDashboardScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppTab.TOPICS_REFERENCE -> {
                            TopicsReferenceScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    activeTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        contentColor = Color(0xFF38BDF8)
    ) {
        NavigationBarItem(
            selected = activeTab == AppTab.STUDENT_GAME,
            onClick = { onTabSelected(AppTab.STUDENT_GAME) },
            icon = { Icon(Icons.Default.Extension, contentDescription = "Game") },
            label = { Text("Game") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F172A),
                selectedTextColor = Color(0xFF38BDF8),
                indicatorColor = Color(0xFF38BDF8),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_student_game")
        )

        NavigationBarItem(
            selected = activeTab == AppTab.TOPICS_REFERENCE,
            onClick = { onTabSelected(AppTab.TOPICS_REFERENCE) },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Trig Guide") },
            label = { Text("Trig Guide") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F172A),
                selectedTextColor = Color(0xFF38BDF8),
                indicatorColor = Color(0xFF38BDF8),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_trig_guide")
        )

        NavigationBarItem(
            selected = activeTab == AppTab.TEACHER_DASHBOARD,
            onClick = { onTabSelected(AppTab.TEACHER_DASHBOARD) },
            icon = { Icon(Icons.Default.School, contentDescription = "Teacher") },
            label = { Text("Teacher") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F172A),
                selectedTextColor = Color(0xFF38BDF8),
                indicatorColor = Color(0xFF38BDF8),
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("nav_teacher_dashboard")
        )
    }
}
