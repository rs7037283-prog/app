package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Support Edge to Edge rendering safely
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
                val savedDrafts by viewModel.savedDrafts.collectAsStateWithLifecycle()
                val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
                val isFirebaseActive by viewModel.isFirebaseActive.collectAsStateWithLifecycle()

                // Register event transmitter for Toast flows
                LaunchedEffect(Unit) {
                    viewModel.toastMessage.collectLatest { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        is AppScreen.Splash -> {
                            SplashScreen()
                        }
                        is AppScreen.Auth -> {
                            AuthScreen(
                                isAuthLoading = isAuthLoading,
                                isFirebaseActive = isFirebaseActive,
                                onLoginClick = { email, password, isSignUp ->
                                    viewModel.login(email, password, isSignUp)
                                }
                            )
                        }
                        is AppScreen.MainTab -> {
                            MainContainer(
                                currentTab = currentScreen as AppScreen.MainTab,
                                userEmail = userEmail,
                                savedDrafts = savedDrafts,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContainer(
    currentTab: AppScreen.MainTab,
    userEmail: String,
    savedDrafts: List<com.example.data.model.Draft>,
    viewModel: MainViewModel
) {
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val generatedCount by viewModel.generatedCount.collectAsStateWithLifecycle()
    val showSubscriptionDialog by viewModel.showSubscriptionDialog.collectAsStateWithLifecycle()

    GlassmorphicBackground {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_container"),
            containerColor = Color.Transparent,
            topBar = {
                HeaderBar(
                    userEmail = userEmail,
                    onLogoutClick = { viewModel.logout() }
                )
            },
            bottomBar = {
                BottomTabBar(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.navigateTo(tab) }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
            // Screen Switch Layout Animation
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_body_fade"
            ) { tab ->
                when (tab) {
                    is AppScreen.MainTab.Home -> {
                        HomeScreen(
                            isPremium = isPremium,
                            generatedCount = generatedCount,
                            onUpgradeClick = { viewModel.toggleSubscriptionDialog(true) },
                            onCancelSubscriptionClick = { viewModel.cancelPremium() },
                            savedDrafts = savedDrafts,
                            onStartPromptingClick = { viewModel.navigateTo(AppScreen.MainTab.Create) },
                            onViewAllDraftsClick = { viewModel.navigateTo(AppScreen.MainTab.Drafts) },
                            onOlderDraftClick = {
                                viewModel.navigateTo(AppScreen.MainTab.Drafts)
                            }
                        )
                    }
                    is AppScreen.MainTab.Create -> {
                        val isRealApiKey by viewModel.isRealApiKey.collectAsStateWithLifecycle()
                        val selectedNiche by viewModel.selectedNiche.collectAsStateWithLifecycle()
                        val selectedTone by viewModel.selectedTone.collectAsStateWithLifecycle()
                        val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
                        val topicInput by viewModel.topicInput.collectAsStateWithLifecycle()
                        val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
                        val generatedDraft by viewModel.generatedDraft.collectAsStateWithLifecycle()

                        // Standalone Hook tool states
                        val hookInputTopic by viewModel.hookInputTopic.collectAsStateWithLifecycle()
                        val hookStyle by viewModel.hookStyle.collectAsStateWithLifecycle()
                        val generatedHooks by viewModel.generatedHooks.collectAsStateWithLifecycle()
                        val isGeneratingHook by viewModel.isGeneratingHook.collectAsStateWithLifecycle()

                        // Standalone Caption tool states
                        val captionInputSummary by viewModel.captionInputSummary.collectAsStateWithLifecycle()
                        val captionFormatStyle by viewModel.captionFormatStyle.collectAsStateWithLifecycle()
                        val generatedCaption by viewModel.generatedCaption.collectAsStateWithLifecycle()
                        val isGeneratingCaption by viewModel.isGeneratingCaption.collectAsStateWithLifecycle()

                        // Standalone Hashtag tool states
                        val hashtagInputTopic by viewModel.hashtagInputTopic.collectAsStateWithLifecycle()
                        val hashtagPlatform by viewModel.hashtagPlatform.collectAsStateWithLifecycle()
                        val generatedHashtags by viewModel.generatedHashtags.collectAsStateWithLifecycle()
                        val isGeneratingHashtags by viewModel.isGeneratingHashtags.collectAsStateWithLifecycle()

                        // Standalone Thumbnail tool states
                        val thumbnailInputConcept by viewModel.thumbnailInputConcept.collectAsStateWithLifecycle()
                        val thumbnailTactic by viewModel.thumbnailTactic.collectAsStateWithLifecycle()
                        val generatedThumbnails by viewModel.generatedThumbnails.collectAsStateWithLifecycle()
                        val isGeneratingThumbnails by viewModel.isGeneratingThumbnails.collectAsStateWithLifecycle()

                        CreateScreen(
                            isPremium = isPremium,
                            isRealApiKey = isRealApiKey,
                            selectedNiche = selectedNiche,
                            onNicheChanged = { viewModel.updateNiche(it) },
                            selectedTone = selectedTone,
                            onToneChanged = { viewModel.updateTone(it) },
                            selectedDuration = selectedDuration,
                            onDurationChanged = { viewModel.updateDuration(it) },
                            topicInput = topicInput,
                            onTopicInputChanged = { viewModel.updateTopic(it) },
                            isGenerating = isGenerating,
                            generatedDraft = generatedDraft,
                            onGenerateClick = { viewModel.generateScript() },
                            onSaveDraftClick = { viewModel.saveDraft(it) },
                            onToastNeeded = { viewModel.emitToast(it) },

                            // Hook tool actions & states
                            hookInputTopic = hookInputTopic,
                            onHookInputTopicChanged = { viewModel.updateHookInputTopic(it) },
                            hookStyle = hookStyle,
                            onHookStyleChanged = { viewModel.updateHookStyle(it) },
                            generatedHooks = generatedHooks,
                            isGeneratingHook = isGeneratingHook,
                            onGenerateHookClick = { viewModel.generateAIHook() },
                            onApplyHookToDraft = { viewModel.applyHookToDraft(it) },

                            // Caption tool actions & states
                            captionInputSummary = captionInputSummary,
                            onCaptionInputSummaryChanged = { viewModel.updateCaptionInputSummary(it) },
                            captionFormatStyle = captionFormatStyle,
                            onCaptionFormatStyleChanged = { viewModel.updateCaptionFormatStyle(it) },
                            generatedCaption = generatedCaption,
                            isGeneratingCaption = isGeneratingCaption,
                            onGenerateCaptionClick = { viewModel.generateAICaption() },
                            onApplyCaptionToDraft = { viewModel.applyCaptionToDraft(it) },

                            // Hashtags tool actions & states
                            hashtagInputTopic = hashtagInputTopic,
                            onHashtagInputTopicChanged = { viewModel.updateHashtagInputTopic(it) },
                            hashtagPlatform = hashtagPlatform,
                            onHashtagPlatformChanged = { viewModel.updateHashtagPlatform(it) },
                            generatedHashtags = generatedHashtags,
                            isGeneratingHashtags = isGeneratingHashtags,
                            onGenerateHashtagsClick = { viewModel.generateAIHashtags() },
                            onApplyHashtagsToDraft = { viewModel.applyHashtagsToDraft(it) },

                            // Thumbnail text overlay tool actions & states
                            thumbnailInputConcept = thumbnailInputConcept,
                            onThumbnailInputConceptChanged = { viewModel.updateThumbnailInputConcept(it) },
                            thumbnailTactic = thumbnailTactic,
                            onThumbnailTacticChanged = { viewModel.updateThumbnailTactic(it) },
                            generatedThumbnails = generatedThumbnails,
                            isGeneratingThumbnails = isGeneratingThumbnails,
                            onGenerateThumbnailsClick = { viewModel.generateAIThumbnails() },
                            onApplyThumbnailTextToDraft = { viewModel.applyThumbnailTextToDraft(it) }
                        )
                    }
                    is AppScreen.MainTab.Drafts -> {
                        DraftsScreen(
                            savedDrafts = savedDrafts,
                            onDeleteDraftClick = { viewModel.deleteDraft(it) },
                            onToastNeeded = { viewModel.emitToast(it) }
                        )
                    }
                }
            }
        }
    }
}

    if (showSubscriptionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleSubscriptionDialog(false) },
            confirmButton = {
                Button(
                    onClick = { viewModel.purchasePremium() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBBF24),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Unlock Premium - $9.99/mo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.toggleSubscriptionDialog(false) }
                ) {
                    Text("Maybe Later", color = Color.White.copy(alpha = 0.5f))
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Subscription Star Icon",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Upgrade to ReelAI Premium",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Take your dynamic viral reels to the absolute next level with unlimited generation credits and ultimate custom capabilities:",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF27272A))
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val proFeatures = listOf(
                            "Unlimited generation credits (No more 3-script limits!)",
                            "Unlock PRO Niche selections: Business, Comedy, and more",
                            "Unlock retention-optimized Dramatic and Humorous tones",
                            "Generate professional full-length 60-second Reels",
                            "Cloud backup & instant cloud synchronization toggles"
                        )
                        proFeatures.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Check Icon",
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = feature,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF27272A))
                    )

                    Text(
                        text = "Instant 3-day money-back guarantee. Cancel anytime with a single tap.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF121214),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(16.dp)
                .border(2.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .testTag("subscription_checkout_dialog")
        )
    }
}

@Composable
fun HeaderBar(
    userEmail: String,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0x3809090B))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity Brand Logo layout
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryAccent, SecondaryAccent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "ReelAI Small Logo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "ReelAI",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
        }

        // Profile Avatar and Logout Action Layout Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18181B))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Logout button",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Simulated premium profile avatar image box
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SecondaryAccent, PrimaryAccent)
                        )
                    )
                    .padding(1.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF1F1F23)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userEmail.take(1).uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BottomTabBar(
    currentTab: AppScreen.MainTab,
    onTabSelected: (AppScreen.MainTab) -> Unit
) {
    NavigationBar(
        containerColor = Color(0x3809090B),
        tonalElevation = 0.dp,
        modifier = Modifier
            .navigationBarsPadding() // Satisfy safe area notches padding mandate
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .testTag("bottom_tab_bar")
    ) {
        NavigationBarItem(
            selected = currentTab is AppScreen.MainTab.Home,
            onClick = { onTabSelected(AppScreen.MainTab.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Tab Icon"
                )
            },
            label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryAccent,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                selectedTextColor = PrimaryAccent,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                indicatorColor = PrimaryAccent.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentTab is AppScreen.MainTab.Create,
            onClick = { onTabSelected(AppScreen.MainTab.Create) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Tab Icon"
                )
            },
            label = { Text("Create", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryAccent,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                selectedTextColor = PrimaryAccent,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                indicatorColor = PrimaryAccent.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_create")
        )

        NavigationBarItem(
            selected = currentTab is AppScreen.MainTab.Drafts,
            onClick = { onTabSelected(AppScreen.MainTab.Drafts) },
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Drafts Tab Icon"
                )
            },
            label = { Text("Drafts", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryAccent,
                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                selectedTextColor = PrimaryAccent,
                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                indicatorColor = PrimaryAccent.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_drafts")
        )
    }
}
