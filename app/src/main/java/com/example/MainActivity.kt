package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LunaTab
import com.example.ui.LunaViewModel
import com.example.ui.screens.AppActionsScreen
import com.example.ui.screens.IctTradingScreen
import com.example.ui.screens.LunaChatScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SettingsHelpScreen
import com.example.ui.theme.LunaAmberTertiary
import com.example.ui.theme.LunaCyanPrimary
import com.example.ui.theme.LunaDarkBackground
import com.example.ui.theme.LunaDarkSurface
import com.example.ui.theme.LunaPurpleSecondary
import com.example.ui.theme.LunaTextPrimary
import com.example.ui.theme.LunaTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LunaViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Runtime Permissions Launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { /* Permissions result handled gracefully in UI */ }

                LaunchedEffect(Unit) {
                    val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }

                val currentTab by viewModel.currentTab.collectAsState()

                // BackHandler: return to ASSISTANT tab if back is pressed on another tab
                BackHandler(enabled = currentTab != LunaTab.ASSISTANT) {
                    viewModel.setTab(LunaTab.ASSISTANT)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LunaDarkBackground,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        LunaCyanPrimary,
                                                        LunaPurpleSecondary,
                                                        Color(0xFF0F172A)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "L",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "LUNA AI",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = LunaTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "हिंदी असिस्टेंट",
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = LunaCyanPrimary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = LunaDarkSurface,
                                titleContentColor = LunaTextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = LunaDarkSurface,
                            tonalElevation = 8.dp
                        ) {
                            val items = listOf(
                                Triple(LunaTab.ASSISTANT, "LUNA", Icons.Default.Mic),
                                Triple(LunaTab.ICT_TRADING, "ICT ट्रेडिंग", Icons.Default.TrendingUp),
                                Triple(LunaTab.APP_ACTIONS, "ऐप्स", Icons.Default.Apps),
                                Triple(LunaTab.MEMORY, "मेमोरी", Icons.Default.Psychology),
                                Triple(LunaTab.SETTINGS, "सेटिंग्स", Icons.Default.Settings)
                            )

                            items.forEach { (tab, label, icon) ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.setTab(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (selected) LunaCyanPrimary else LunaTextSecondary
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) LunaCyanPrimary else LunaTextSecondary
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = LunaCyanPrimary,
                                        indicatorColor = LunaCyanPrimary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
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
                        when (currentTab) {
                            LunaTab.ASSISTANT -> LunaChatScreen(viewModel = viewModel)
                            LunaTab.ICT_TRADING -> IctTradingScreen(viewModel = viewModel)
                            LunaTab.APP_ACTIONS -> AppActionsScreen(viewModel = viewModel)
                            LunaTab.MEMORY -> MemoryScreen(viewModel = viewModel)
                            LunaTab.SETTINGS -> SettingsHelpScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
