package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuickSosDialog
import com.example.ui.components.SerenityTopBar
import com.example.ui.screens.BreathingScreen
import com.example.ui.screens.MindJournalScreen
import com.example.ui.screens.ReliefZenScreen
import com.example.ui.screens.SoundscapesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SageGreenPrimary
import com.example.viewmodel.SerenityViewModel

enum class SerenityTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    RELIEVE(
        title = "Relieve",
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        testTag = "tab_relieve"
    ),
    BREATHE(
        title = "Breathe",
        selectedIcon = Icons.Filled.Air,
        unselectedIcon = Icons.Outlined.Air,
        testTag = "tab_breathe"
    ),
    SOUNDSCAPES(
        title = "Ambiance",
        selectedIcon = Icons.Filled.GraphicEq,
        unselectedIcon = Icons.Outlined.GraphicEq,
        testTag = "tab_soundscapes"
    ),
    MIND(
        title = "Mind Log",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        testTag = "tab_mind"
    )
}

class MainActivity : ComponentActivity() {
    private val viewModel: SerenityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SerenityApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SerenityApp(viewModel: SerenityViewModel) {
    var currentTab by remember { mutableStateOf(SerenityTab.RELIEVE) }
    var showSosDialog by remember { mutableStateOf(false) }

    // BackHandler: return to primary Relieve tab if in sub-tab
    BackHandler(enabled = currentTab != SerenityTab.RELIEVE) {
        currentTab = SerenityTab.RELIEVE
    }

    if (showSosDialog) {
        QuickSosDialog(
            onDismiss = { showSosDialog = false },
            onStepAdvanced = { viewModel.haptic.gentlePulse() }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            SerenityTopBar(
                viewModel = viewModel,
                onOpenSos = { showSosDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                SerenityTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                viewModel.haptic.gentlePulse()
                                currentTab = tab
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SageGreenPrimary,
                            selectedTextColor = SageGreenPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabContentTransition"
            ) { target ->
                when (target) {
                    SerenityTab.RELIEVE -> ReliefZenScreen(viewModel = viewModel)
                    SerenityTab.BREATHE -> BreathingScreen(viewModel = viewModel)
                    SerenityTab.SOUNDSCAPES -> SoundscapesScreen(viewModel = viewModel)
                    SerenityTab.MIND -> MindJournalScreen(viewModel = viewModel)
                }
            }
        }
    }
}
