package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.model.CallStatus
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.service.WebRTCManager

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isConfigLoaded by viewModel.isConfigLoaded.collectAsState()
                
                // Topmost fallback block if configuration failed to load
                if (!isConfigLoaded) {
                    ConfigErrorScreen()
                } else {
                    val navController = rememberNavController()
                    val callStatus by WebRTCManager.getInstance().currentCallStatus.collectAsState()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(
                                navController = navController,
                                startDestination = "splash",
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable("splash") {
                                    SplashScreen(onNavigateToAuth = {
                                        navController.navigate("login") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    })
                                }

                                composable("login") {
                                    LoginScreen(
                                        viewModel = viewModel,
                                        onLoginSuccess = {
                                            navController.navigate("dashboard") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onNavigateToRegister = { navController.navigate("register") },
                                        onNavigateToForgot = { navController.navigate("forgot") }
                                    )
                                }

                                composable("register") {
                                    RegisterScreen(
                                        viewModel = viewModel,
                                        onRegisterSuccess = {
                                            navController.navigate("login") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        },
                                        onNavigateToLogin = { navController.navigate("login") }
                                    )
                                }

                                composable("forgot") {
                                    ForgotPasswordScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                composable("dashboard") {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        onNavigateToChat = { chatId, receiverId ->
                                            navController.navigate("chat_detail/$chatId/$receiverId")
                                        },
                                        onNavigateToGroup = { groupId ->
                                            navController.navigate("group_detail/$groupId")
                                        },
                                        onNavigateToPrivacy = { navController.navigate("privacy") },
                                        onLogout = {
                                            navController.navigate("login") {
                                                popUpTo("dashboard") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable(
                                    route = "chat_detail/{chatId}/{receiverId}",
                                    arguments = listOf(
                                        navArgument("chatId") { type = NavType.StringType },
                                        navArgument("receiverId") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                                    val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
                                    ChatDetailScreen(
                                        chatId = chatId,
                                        receiverId = receiverId,
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                composable(
                                    route = "group_detail/{groupId}",
                                    arguments = listOf(
                                        navArgument("groupId") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                                    GroupDetailScreen(
                                        groupId = groupId,
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }

                                composable("privacy") {
                                    PrivacyScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }

                            // Dynamic Overlapping Calling View (Triggers on Calling Events)
                            AnimatedVisibility(
                                visible = callStatus != CallStatus.DISCONNECTED && callStatus != CallStatus.FAILED,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                            ) {
                                CallScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        // Dynamic termination handled inside CallScreen
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
