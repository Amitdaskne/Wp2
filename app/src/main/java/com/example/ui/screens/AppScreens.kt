package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.service.WebRTCManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -----------------------------------------------------------------
// 1. CONFIGURATION ERROR SCREEN
// -----------------------------------------------------------------
@Composable
fun ConfigErrorScreen() {
    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Config Error",
                tint = CyberAccent,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CONFIGURATION BREACH",
                color = CyberAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(16.dp))

            CyberCard(borderColor = CyberAccent) {
                Text(
                    text = "CRITICAL: config.json is missing or corrupted inside the App assets space directory.\n\n" +
                            "To establish AmitCHAT connectivity, make sure a valid config.json exists with correct Firebase and Cloudinary properties:\n\n" +
                            "Path: app/src/main/assets/config.json\n\n" +
                            "Initializing fallback client sandbox...",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "SANDBOX CONNECTED SUCCESSFULLY",
                color = CyberGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------
// 2. SPLASH SCREEN
// -----------------------------------------------------------------
@Composable
fun SplashScreen(onNavigateToAuth: () -> Unit) {
    var loadingText by remember { mutableStateOf("BOOTING NETWORKS...") }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(600)
        loadingText = "BYPASSING PROTOCOLS..."
        progressAnim.animateTo(0.4f, animationSpec = tween(800))
        delay(400)
        loadingText = "LOADING DECRYPTORS..."
        progressAnim.animateTo(0.8f, animationSpec = tween(600))
        delay(300)
        loadingText = "AMITCHAT DEC-SEC CONNECTED."
        progressAnim.animateTo(1.0f, animationSpec = tween(400))
        delay(500)
        onNavigateToAuth()
    }

    CyberpunkBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Cyber Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Logo",
                    tint = CyberPrimary,
                    modifier = Modifier.size(84.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AmitCHAT",
                    color = CyberPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.SansSerif
                )

                Text(
                    text = "ULTIMATE CYBERPUNK CONNECTED",
                    color = CyberSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Progress loading bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = loadingText,
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .border(1.dp, CyberPrimary.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressAnim.value)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(CyberPrimary, CyberAccent)
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 3. AUTHENTICATION SCREENS
// -----------------------------------------------------------------
@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ACCESS TERMINAL",
                color = CyberPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "IDENTIFY YOUR PAYLOAD",
                color = CyberSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(32.dp))

            CyberCard {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("EMAIL ADDR") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f),
                        focusedLabelColor = CyberPrimary,
                        unfocusedLabelColor = CyberSecondary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("PASS SECURE") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f),
                        focusedLabelColor = CyberPrimary,
                        unfocusedLabelColor = CyberSecondary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMsg.uppercase(),
                        color = CyberAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    CyberButton(
                        text = "ESTABLISH LINK",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (email.isBlank() || password.isBlank()) {
                            errorMsg = "Empty payloads detected"
                            return@CyberButton
                        }
                        isLoading = true
                        errorMsg = ""
                        viewModel.login(
                            email = email,
                            password = password,
                            onSuccess = {
                                isLoading = false
                                onLoginSuccess()
                            },
                            onError = {
                                isLoading = false
                                errorMsg = it
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RECOVER PASS",
                    color = CyberSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { onNavigateToForgot() }
                )

                Text(
                    text = "NEW OPERATOR Registration",
                    color = CyberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: AppViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val context = LocalContext.current
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri
    }

    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CONSTRUCT MATRIX",
                color = CyberPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "SIGN NEW NODE REGISTRY",
                color = CyberSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(24.dp))

            CyberCard {
                // Photo Picker
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CyberGray)
                        .border(1.dp, CyberPrimary, CircleShape)
                        .clickable { pickerLauncher.launch("image/*") }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Selected Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Select Photo",
                            tint = CyberPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("FULL CODENAME") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("ALIAS USERNAME") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("SECURE EMAIL") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("PASSKEY PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    label = { Text("VALIDATE PASSKEY") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberSecondary.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMsg.uppercase(),
                        color = CyberAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    CyberButton(
                        text = "CONSTRUCT IDENTITY",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (email.isBlank() || pass.isBlank() || name.isBlank() || username.isBlank()) {
                            errorMsg = "Empty parameters"
                            return@CyberButton
                        }
                        if (pass != confirmPass) {
                            errorMsg = "PIN mismatch"
                            return@CyberButton
                        }
                        isLoading = true
                        errorMsg = ""
                        viewModel.register(
                            email = email,
                            pass = pass,
                            name = name,
                            username = username,
                            photoUri = photoUri,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "Verification signal sent!", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess()
                            },
                            onError = {
                                isLoading = false
                                errorMsg = it
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ALREADY HAS DECRYPTOR? SECURE SIGN IN",
                color = CyberSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var diagnosticMsg by remember { mutableStateOf("") }
    val context = LocalContext.current

    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MEM LINK RESET",
                color = CyberPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(16.dp))

            CyberCard {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("OPERATOR EMAIL") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                if (diagnosticMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(diagnosticMsg, color = CyberPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isSending) {
                    CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    CyberButton(text = "FORCE BEACON RESET", modifier = Modifier.fillMaxWidth()) {
                        if (email.isBlank()) return@CyberButton
                        isSending = true
                        viewModel.forgotPassword(email, onSuccess = {
                            isSending = false
                            diagnosticMsg = "RESET SIGNAL DISPATCHED SUCCESSFULLY."
                        }, onError = {
                            isSending = false
                            diagnosticMsg = "TRANSMISSION ERROR: " + it.uppercase()
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TERMINATE TERMINAL PROCESS",
                color = CyberSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable { onNavigateBack() }
            )
        }
    }
}

// -----------------------------------------------------------------
// 4. MAIN DASHBOARD SHELL
// -----------------------------------------------------------------
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToChat: (chatId: String, receiverId: String) -> Unit,
    onNavigateToGroup: (groupId: String) -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.startListeningToChats()
        viewModel.startListeningToGroups()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CyberBg,
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            color = CyberPrimary.copy(alpha = 0.15f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // 1. CHATS
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(imageVector = if (selectedTab == 0) Icons.Default.ChatBubble else Icons.Default.ChatBubbleOutline, contentDescription = "Chats") },
                    label = { Text("CHATS", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        unselectedIconColor = CyberSecondary.copy(alpha = 0.6f),
                        selectedTextColor = CyberPrimary,
                        unselectedTextColor = CyberSecondary.copy(alpha = 0.6f),
                        indicatorColor = CyberPrimary.copy(alpha = 0.1f)
                    )
                )

                // 2. STATUS
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(imageVector = if (selectedTab == 1) Icons.Default.Camera else Icons.Default.CameraAlt, contentDescription = "Status") },
                    label = { Text("STORIES", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        unselectedIconColor = CyberSecondary.copy(alpha = 0.6f),
                        selectedTextColor = CyberPrimary,
                        unselectedTextColor = CyberSecondary.copy(alpha = 0.6f),
                        indicatorColor = CyberPrimary.copy(alpha = 0.1f)
                    )
                )

                // 3. CALLS
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(imageVector = if (selectedTab == 2) Icons.Default.Call else Icons.Default.Phone, contentDescription = "Calls") },
                    label = { Text("CHANNELS", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        unselectedIconColor = CyberSecondary.copy(alpha = 0.6f),
                        selectedTextColor = CyberPrimary,
                        unselectedTextColor = CyberSecondary.copy(alpha = 0.6f),
                        indicatorColor = CyberPrimary.copy(alpha = 0.1f)
                    )
                )

                // 4. GROUPS
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(imageVector = if (selectedTab == 3) Icons.Default.Groups2 else Icons.Default.Group, contentDescription = "Groups") },
                    label = { Text("NETS", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        unselectedIconColor = CyberSecondary.copy(alpha = 0.6f),
                        selectedTextColor = CyberPrimary,
                        unselectedTextColor = CyberSecondary.copy(alpha = 0.6f),
                        indicatorColor = CyberPrimary.copy(alpha = 0.1f)
                    )
                )

                // 5. SETTINGS
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(imageVector = if (selectedTab == 4) Icons.Default.Settings else Icons.Default.SettingsSuggest, contentDescription = "Settings") },
                    label = { Text("ACCESS", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        unselectedIconColor = CyberSecondary.copy(alpha = 0.6f),
                        selectedTextColor = CyberPrimary,
                        unselectedTextColor = CyberSecondary.copy(alpha = 0.6f),
                        indicatorColor = CyberPrimary.copy(alpha = 0.1f)
                    )
                )
            }
        },
        containerColor = CyberBg
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ChatsTab(viewModel, onNavigateToChat)
                1 -> StatusTab(viewModel)
                2 -> CallsTab(viewModel)
                3 -> GroupsTab(viewModel, onNavigateToGroup)
                4 -> SettingsTab(viewModel, onNavigateToPrivacy, onLogout)
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 1: CHATS LIST
// -----------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatsTab(
    viewModel: AppViewModel,
    onNavigateToChat: (chatId: String, receiverId: String) -> Unit
) {
    val chats by viewModel.chats.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val selfUser by viewModel.currentUser.collectAsState()

    var showUsersDialog by remember { mutableStateOf(false) }

    CyberpunkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DECRYPTED DIRECT",
                        color = CyberPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "DIRECT SYNC ROOMS ACTIVE: " + chats.size,
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { showUsersDialog = true },
                    modifier = Modifier
                        .background(CyberPrimary.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, CyberPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat room",
                        tint = CyberPrimary
                    )
                }
            }

            if (chats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO COMS CHANNELS ONLINE.",
                        color = CyberSecondary.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(chats) { chat ->
                        val remoteId = if (chat.user1Id == selfUser?.uid) chat.user2Id else chat.user1Id
                        val remoteUser = allUsers.find { it.uid == remoteId }

                        CyberCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            onClick = { onNavigateToChat(chat.id, remoteId) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User Avatar
                                Box(
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(CyberGray)
                                            .border(
                                                1.5.dp,
                                                if (remoteUser?.onlineStatus == true) CyberGreen else CyberSecondary,
                                                CircleShape
                                            )
                                    ) {
                                        AsyncImage(
                                            model = remoteUser?.profilePhoto ?: "",
                                            contentDescription = "avatar",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (remoteUser?.onlineStatus == true) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(CyberGreen, CircleShape)
                                                .border(2.dp, CyberBg, CircleShape)
                                                .align(Alignment.BottomEnd)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = remoteUser?.name ?: "Unknown Node",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = chat.lastMessageText,
                                        color = CyberSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val unread = if (chat.user1Id == selfUser?.uid) chat.unreadCountUser1 else chat.unreadCountUser2
                                    if (unread > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(CyberAccent, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unread.toString(),
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUsersDialog) {
        AlertDialog(
            onDismissRequest = { showUsersDialog = false },
            containerColor = CyberGray,
            title = { Text("ESTABLISH NEW SEC-NODE ROOM", color = CyberPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    val others = allUsers.filter { it.uid != selfUser?.uid }
                    items(others) { other ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showUsersDialog = false
                                    val generatedId = "chat_${selfUser?.uid}_${other.uid}"
                                    onNavigateToChat(generatedId, other.uid)
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberBg)
                            ) {
                                AsyncImage(
                                    model = other.profilePhoto,
                                    contentDescription = "avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(other.name, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showUsersDialog = false }) {
                    Text("ABORT")
                }
            }
        )
    }
}

// -----------------------------------------------------------------
// TAB 2: EXPIRED STATUS STORIES
// -----------------------------------------------------------------
@Composable
fun StatusTab(viewModel: AppViewModel) {
    val statuses by viewModel.statuses.collectAsState()
    val selfUser by viewModel.currentUser.collectAsState()

    val context = LocalContext.current
    val statusPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadStatus(uri, StatusType.IMAGE)
            Toast.makeText(context, "Story media queued for grid injection...", Toast.LENGTH_SHORT).show()
        }
    }

    CyberpunkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TEMPORARY FEED",
                        color = CyberPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "DESTRUCTS IN 24 HOURS INJECTS",
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { statusPicker.launch("image/*") },
                    modifier = Modifier
                        .background(CyberPrimary.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, CyberPrimary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Add status story", tint = CyberPrimary)
                }
            }

            if (statuses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO STORIES ONLINE IN MATRIX CURRENTLY.",
                        color = CyberSecondary.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(statuses) { status ->
                        CyberCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = status.mediaUrl,
                                    contentDescription = "status story image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                                )

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(CyberGray)
                                    ) {
                                        AsyncImage(model = status.userPhoto, contentDescription = "avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(status.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 3: CHANNELS (CALL LOG LOGS)
// -----------------------------------------------------------------
@Composable
fun CallsTab(viewModel: AppViewModel) {
    val callHistory by viewModel.callHistory.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var showChooseUserDialog by remember { mutableStateOf(false) }

    CyberpunkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FREQUENCY NODES",
                        color = CyberPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "HISTORIC CALL FREQUENCIES LOGGED",
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { showChooseUserDialog = true },
                    modifier = Modifier
                        .background(CyberPrimary.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, CyberPrimary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.PhoneCallback, contentDescription = "New Call", tint = CyberPrimary)
                }
            }

            if (callHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO HISTORIC RECORDS IN SIGNAL SECTOR.",
                        color = CyberSecondary.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(callHistory) { cLog ->
                        CyberCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (cLog.type == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Phone,
                                    contentDescription = "type",
                                    tint = CyberPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = if (cLog.callerName == "You") cLog.receiverName else cLog.callerName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = cLog.status.name + " • " + cLog.duration + "s",
                                        color = CyberSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showChooseUserDialog) {
        val selfUser by viewModel.currentUser.collectAsState()
        AlertDialog(
            onDismissRequest = { showChooseUserDialog = false },
            containerColor = CyberGray,
            title = { Text("TRIGGER COM SIGNAL BEACON", color = CyberPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    val others = allUsers.filter { it.uid != selfUser?.uid }
                    items(others) { other ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showChooseUserDialog = false
                                    viewModel.initiateCall(other.uid, CallType.VIDEO)
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(CyberBg)) {
                                AsyncImage(model = other.profilePhoto, contentDescription = "avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(other.name, color = Color.White, fontSize = 14.sp)
                                Text("@" + other.username, color = CyberSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// -----------------------------------------------------------------
// TAB 4: GROUPS (NETS CHANNEL)
// -----------------------------------------------------------------
@Composable
fun GroupsTab(
    viewModel: AppViewModel,
    onNavigateToGroup: (groupId: String) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val selfUser by viewModel.currentUser.collectAsState()

    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var gName by remember { mutableStateOf("") }
    var gDesc by remember { mutableStateOf("") }
    val chosenList = remember { mutableStateListOf<String>() }

    CyberpunkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DECENTRAL CHANNELS",
                        color = CyberPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "MULTIPROCESS OPERATOR CHANNELS",
                        color = CyberSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { showCreateGroupDialog = true },
                    modifier = Modifier
                        .background(CyberPrimary.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, CyberPrimary, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Groups, contentDescription = "Create Group", tint = CyberPrimary)
                }
            }

            if (groups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO CHANNELS DISCOVERED ON GRID.",
                        color = CyberSecondary.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(groups) { group ->
                        CyberCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            onClick = { onNavigateToGroup(group.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(CyberGray)
                                ) {
                                    AsyncImage(model = group.photoUrl, contentDescription = "photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(group.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(group.description, color = CyberSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateGroupDialog) {
        val scope = rememberCoroutineScope()
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            containerColor = CyberGray,
            title = { Text("INITIALIZE CHANNEL CORE", color = CyberPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = gName,
                        onValueChange = { gName = it },
                        label = { Text("CHANNEL NAME") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = gDesc,
                        onValueChange = { gDesc = it },
                        label = { Text("MISSION BOUNDARIES DESCRIPTION") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("TAG IN OPERATORS:", color = CyberSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                    val filterOthers = allUsers.filter { it.uid != selfUser?.uid }
                    filterOthers.forEach { other ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (chosenList.contains(other.uid)) chosenList.remove(other.uid) else chosenList.add(other.uid)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = chosenList.contains(other.uid),
                                onCheckedChange = {
                                    if (chosenList.contains(other.uid)) chosenList.remove(other.uid) else chosenList.add(other.uid)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(other.name, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                CyberButton(text = "CONSTRUCT") {
                    if (gName.isBlank()) return@CyberButton
                    viewModel.createGroup(gName, gDesc, null, chosenList.toList(), onSuccess = {
                        showCreateGroupDialog = false
                    }, onError = {})
                }
            }
        )
    }
}

// -----------------------------------------------------------------
// TAB 5: STORAGE/SETTINGS PANEL
// -----------------------------------------------------------------
@Composable
fun SettingsTab(
    viewModel: AppViewModel,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit
) {
    val selfUser by viewModel.currentUser.collectAsState()
    var editMode by remember { mutableStateOf(false) }
    var codename by remember { mutableStateOf(selfUser?.name ?: "") }
    var aliasUsername by remember { mutableStateOf(selfUser?.username ?: "") }
    var quoteAbout by remember { mutableStateOf(selfUser?.about ?: "") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "OPERATOR IDENT",
                color = CyberPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(20.dp))

            CyberCard {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(CyberBg)
                        .border(2.dp, CyberPrimary, CircleShape)
                        .align(Alignment.CenterHorizontally)
                ) {
                    AsyncImage(
                        model = selfUser?.profilePhoto ?: "",
                        contentDescription = "avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (editMode) {
                    OutlinedTextField(
                        value = codename,
                        onValueChange = { codename = it },
                        label = { Text("CODENAME") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = aliasUsername,
                        onValueChange = { aliasUsername = it },
                        label = { Text("USERNAME") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quoteAbout,
                        onValueChange = { quoteAbout = it },
                        label = { Text("STATUS LINE ABOUT") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CyberButton(text = "COMMIT SYNC PROTOCOL", modifier = Modifier.fillMaxWidth()) {
                        viewModel.updateProfile(codename, aliasUsername, quoteAbout, null, onSuccess = {
                            editMode = false
                        }, onError = {})
                    }
                } else {
                    Text(
                        text = selfUser?.name ?: "No Codename",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@" + (selfUser?.username ?: "no_alias"),
                            color = CyberSecondary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(selfUser?.username ?: ""))
                            Toast.makeText(context, "Username copied to matrix buffer!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberPrimary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selfUser?.about ?: "A clean mind is a secured mind.",
                        color = CyberOnBg,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { editMode = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, CyberPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("REWRITE MATRIX IDENTITY", color = CyberPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub links
            CyberCard {
                ListItem(
                    headlineContent = { Text("Privacy Controls", color = Color.White) },
                    supportingContent = { Text("Configure profile exposure and blacklist", color = CyberSecondary, fontSize = 11.sp) },
                    leadingContent = { Icon(imageVector = Icons.Default.Security, contentDescription = "privacy", tint = CyberPrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onNavigateToPrivacy() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ListItem(
                    headlineContent = { Text("Flush Cache & Memory", color = Color.White) },
                    supportingContent = { Text("Wipe payload history logs", color = CyberSecondary, fontSize = 11.sp) },
                    leadingContent = { Icon(imageVector = Icons.Default.Cached, contentDescription = "flush", tint = CyberPrimary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Sector Cache cleared and optimized.", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CyberButton(text = "LOGOUT TERMINAL", modifier = Modifier.fillMaxWidth(), color = CyberAccent) {
                    viewModel.logout()
                    onLogout()
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// PRIVACY SETTINGS PANEL
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var hideLastSeen by remember { mutableStateOf(false) }
    var hideProfilePhoto by remember { mutableStateOf(false) }
    var hideAbout by remember { mutableStateOf(false) }

    CyberpunkBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("PRIVACY ENHANCEMENT", color = CyberPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back", tint = CyberPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CyberBg)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                CyberCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mask Last Seen Timestamp", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Prevent endpoints from tracking online status", color = CyberSecondary, fontSize = 11.sp)
                        }
                        Switch(checked = hideLastSeen, onCheckedChange = { hideLastSeen = it })
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Cloak Profile Cover Image", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Strictly expose avatar to matrix validated contacts", color = CyberSecondary, fontSize = 11.sp)
                        }
                        Switch(checked = hideProfilePhoto, onCheckedChange = { hideProfilePhoto = it })
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Classify About Quote Block", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Encrypt secondary quote data payload", color = CyberSecondary, fontSize = 11.sp)
                        }
                        Switch(checked = hideAbout, onCheckedChange = { hideAbout = it })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("SECTOR DEBARRED (BLACKLISTED NODES)", color = CyberPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                CyberCard {
                    if (blockedUsers.isEmpty()) {
                        Text("No nodes debarred in blacklist.", color = CyberSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(8.dp))
                    } else {
                        blockedUsers.forEach { bUid ->
                            val bUser = allUsers.find { it.uid == bUid }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(bUser?.name ?: "Unknown Operator Node", color = Color.White, fontSize = 13.sp)
                                Text(
                                    text = "WIPE SUSPENSION",
                                    color = CyberAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.clickable { viewModel.toggleBlockUser(bUid) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 5. CHAT ROOM / DIRECT MESSAGES DETAIL
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    receiverId: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.activeChatMessages.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val selfUser by viewModel.currentUser.collectAsState()

    val receiverUser = allUsers.find { it.uid == receiverId }

    var typedText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Media launcher
    val context = LocalContext.current
    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.sendMessage(
                chatId = chatId,
                receiverId = receiverId,
                text = "Document Payload transmission",
                type = MessageType.DOCUMENT,
                mediaUri = uri
            )
            Toast.makeText(context, "Encrypted payload launched on channels...", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(chatId) {
        viewModel.openChatMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CyberpunkBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(CyberGray)) {
                                AsyncImage(model = receiverUser?.profilePhoto ?: "", contentDescription = "receiver avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(receiverUser?.name ?: "Unknown Operator", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (receiverUser?.onlineStatus == true) {
                                    Text("ONLINE NODE", color = CyberGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                } else {
                                    Text("CHANNEL OFFLINE", color = CyberSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back", tint = CyberPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.initiateCall(receiverId, CallType.VOICE) }) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "voice call", tint = CyberPrimary)
                        }
                        IconButton(onClick = { viewModel.initiateCall(receiverId, CallType.VIDEO) }) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = "video call", tint = CyberPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBg),
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = CyberPrimary.copy(alpha = 0.15f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Messages log column
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        val isMine = message.senderId == selfUser?.uid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .background(
                                        color = if (isMine) CyberCardBg else CyberGray.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isMine) CyberAccent.copy(alpha = 0.4f) else CyberPrimary.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    if (message.type == MessageType.DOCUMENT) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Attachment, contentDescription = "doc", tint = CyberPrimary)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(message.mediaFileName, color = CyberPrimary, fontSize = 12.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    Text(
                                        text = message.text,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "%02d:%02d".format((message.timestamp / 3600000 % 24), (message.timestamp / 60000 % 60)),
                                            color = CyberSecondary.copy(alpha = 0.6f),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (isMine) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "indicator",
                                                tint = CyberPrimary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Keyboard input bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { docPicker.launch("*/*") }) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add payload file", tint = CyberPrimary)
                    }

                    OutlinedTextField(
                        value = typedText,
                        onValueChange = { typedText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("TYPED PAYLOAD DECRYPT...", color = CyberSecondary, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberSecondary.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (typedText.isNotBlank()) {
                                viewModel.sendMessage(chatId, receiverId, typedText)
                                typedText = ""
                            }
                        },
                        modifier = Modifier
                            .background(CyberPrimary, CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send", tint = CyberBg, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 6. CHANNEL CHATS (GROUP CHAT ROOM)
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.activeGroupMessages.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val groupObj = groups.find { it.id == groupId }

    var inputMsg by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(groupId) {
        viewModel.openGroupMessages(groupId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CyberpunkBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(groupObj?.name ?: "Nexus Core Net", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text((groupObj?.memberIds?.size ?: 0).toString() + " Nodes active on line", color = CyberSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back", tint = CyberPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBg),
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = CyberPrimary.copy(alpha = 0.15f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { gmsg ->
                        val isOwn = gmsg.senderId == viewModel.currentUser.value?.uid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
                        ) {
                            CyberCard(
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(gmsg.text, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMsg,
                        onValueChange = { inputMsg = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("BROADCAST FREQUENCY METRIC...", color = CyberSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputMsg.isNotBlank()) {
                                viewModel.sendGroupMessage(groupId, inputMsg)
                                inputMsg = ""
                            }
                        },
                        modifier = Modifier
                            .background(CyberPrimary, CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send", tint = CyberBg)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// 7. CALLS SIGNALING (INCOMING & OUTGOING INTERACTION)
// -----------------------------------------------------------------
@Composable
fun CallScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val callStatus by WebRTCManager.getInstance().currentCallStatus.collectAsState()
    val callDurationSec by WebRTCManager.getInstance().callDurationSec.collectAsState()
    val isMuted by WebRTCManager.getInstance().isMuted.collectAsState()
    val isSpeakerOn by WebRTCManager.getInstance().isSpeakerOn.collectAsState()
    val isCameraEnabled by WebRTCManager.getInstance().isCameraEnabled.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(callStatus) {
        if (callStatus == CallStatus.COMPLETED || callStatus == CallStatus.REJECTED || callStatus == CallStatus.DISCONNECTED) {
            delay(1000)
            onNavigateBack()
        }
    }

    CyberpunkBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Signal State bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "FREQUENCY TRANSMISSION LINK",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(CyberGray)
                        .border(1.dp, CyberAccent, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = "calling",
                        tint = CyberPrimary,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "OPERATOR SIGNAL METRIC",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = callStatus.name + " • %02d:%02d".format(callDurationSec / 60, callDurationSec % 60),
                    color = CyberPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Connection action triggers
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // MUTE Local transmission feed
                    IconButton(
                        onClick = { WebRTCManager.getInstance().toggleMic() },
                        modifier = Modifier
                            .background(if (isMuted) CyberAccent else CyberGray, CircleShape)
                            .border(1.dp, CyberPrimary, CircleShape)
                            .size(54.dp)
                    ) {
                        Icon(imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = "mute", tint = Color.White)
                    }

                    // CAMERA local streams trigger
                    IconButton(
                        onClick = { WebRTCManager.getInstance().toggleCamera() },
                        modifier = Modifier
                            .background(if (isCameraEnabled) CyberGray else CyberAccent, CircleShape)
                            .border(1.dp, CyberPrimary, CircleShape)
                            .size(54.dp)
                    ) {
                        Icon(imageVector = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff, contentDescription = "camera toggle", tint = Color.White)
                    }

                    // AUDIO Output routing
                    IconButton(
                        onClick = { WebRTCManager.getInstance().toggleSpeaker(context) },
                        modifier = Modifier
                            .background(if (isSpeakerOn) CyberPrimary else CyberGray, CircleShape)
                            .border(1.dp, CyberPrimary, CircleShape)
                            .size(54.dp)
                    ) {
                        Icon(imageVector = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown, contentDescription = "speaker routing", tint = if (isSpeakerOn) CyberBg else Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                if (callStatus == CallStatus.RINGING) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        CyberButton(
                            text = "DECLINE CONNECTION",
                            modifier = Modifier.weight(1f),
                            color = CyberAccent
                        ) {
                            WebRTCManager.getInstance().rejectCall()
                        }

                        CyberButton(
                            text = "ACCEPT SYNC",
                            modifier = Modifier.weight(1f),
                            color = CyberPrimary
                        ) {
                            WebRTCManager.getInstance().acceptCall()
                        }
                    }
                } else {
                    CyberButton(
                        text = "TERMINATE INTEGRATION BEACON",
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberAccent
                    ) {
                        WebRTCManager.getInstance().endCall()
                    }
                }
            }
        }
    }
}
