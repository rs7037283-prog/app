package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Draft
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent

@Composable
fun CreateScreen(
    isPremium: Boolean,
    isRealApiKey: Boolean,
    selectedNiche: String,
    onNicheChanged: (String) -> Unit,
    selectedTone: String,
    onToneChanged: (String) -> Unit,
    selectedDuration: String,
    onDurationChanged: (String) -> Unit,
    topicInput: String,
    onTopicInputChanged: (String) -> Unit,
    isGenerating: Boolean,
    generatedDraft: Draft?,
    onGenerateClick: () -> Unit,
    onSaveDraftClick: (Draft) -> Unit,
    onToastNeeded: (String) -> Unit,

    // Standalone Hook tool states
    hookInputTopic: String,
    onHookInputTopicChanged: (String) -> Unit,
    hookStyle: String,
    onHookStyleChanged: (String) -> Unit,
    generatedHooks: List<String>,
    isGeneratingHook: Boolean,
    onGenerateHookClick: () -> Unit,
    onApplyHookToDraft: (String) -> Unit,

    // Standalone Caption tool states
    captionInputSummary: String,
    onCaptionInputSummaryChanged: (String) -> Unit,
    captionFormatStyle: String,
    onCaptionFormatStyleChanged: (String) -> Unit,
    generatedCaption: String,
    isGeneratingCaption: Boolean,
    onGenerateCaptionClick: () -> Unit,
    onApplyCaptionToDraft: (String) -> Unit,

    // Standalone Hashtags tool states
    hashtagInputTopic: String,
    onHashtagInputTopicChanged: (String) -> Unit,
    hashtagPlatform: String,
    onHashtagPlatformChanged: (String) -> Unit,
    generatedHashtags: String,
    isGeneratingHashtags: Boolean,
    onGenerateHashtagsClick: () -> Unit,
    onApplyHashtagsToDraft: (String) -> Unit,

    // Standalone Thumbnail tool states
    thumbnailInputConcept: String,
    onThumbnailInputConceptChanged: (String) -> Unit,
    thumbnailTactic: String,
    onThumbnailTacticChanged: (String) -> Unit,
    generatedThumbnails: List<String>,
    isGeneratingThumbnails: Boolean,
    onGenerateThumbnailsClick: () -> Unit,
    onApplyThumbnailTextToDraft: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    var activeTab by remember { mutableStateOf(0) }
    var expandedTool by remember { mutableStateOf(0) }

    val nicheList = listOf(
        NicheItem("Tech & AI", Icons.Default.Settings),
        NicheItem("Finance", Icons.Default.Star),
        NicheItem("Fitness", Icons.Default.PlayArrow),
        NicheItem("Lifestyle", Icons.Default.Favorite),
        NicheItem("Business", Icons.Default.Info),
        NicheItem("Comedy", Icons.Default.Face)
    )

    val tones = listOf("Energetic", "Educational", "Dramatic", "Casual", "Humorous")
    val durations = listOf("15s", "30s", "60s")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .testTag("create_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title Header
        Column {
            Text(
                text = "Script Studio",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Let AI custom-craft your next video script",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        // Live API Connection status banner conforming to AI Studio context guidelines
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isRealApiKey) Color(0xFF10B981).copy(alpha = 0.25f) else Color(0xFFEC4899).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRealApiKey) Color(0xFF0F1E15) else Color(0xFF1E0E1B)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (isRealApiKey) Color(0xFF10B981) else Color(0xFFEC4899))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRealApiKey) "Gemini API Active" else "AI Sandbox Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isRealApiKey) Color(0xFF10B981) else Color(0xFFEC4899)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isRealApiKey) {
                            "Sequencing via live Google gemini-3.5-flash for maximum virality."
                        } else {
                            "Procedural templates active. Configure GEMINI_API_KEY in the AI Studio Secrets panel."
                        },
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Beautiful modern slider tab control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF18181B))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabTitles = listOf("Full Script Studio", "Stand-Alone Toolkits")
            tabTitles.forEachIndexed { index, title ->
                val isSelected = activeTab == index
                val tabBrush = if (isSelected) {
                    Brush.linearGradient(colors = listOf(PrimaryAccent, SecondaryAccent))
                } else {
                    Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(tabBrush)
                        .clickable { activeTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        if (activeTab == 0) {
            // Niche Selector Flow Header
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Content Niche",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                letterSpacing = 0.5.sp
            )

            // Dynamic grid using safe standard Row layouts chunked into groups of 3
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                nicheList.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                         rowItems.forEach { item ->
                            val isSelected = item.name == selectedNiche
                            val isLocked = !isPremium && (item.name == "Business" || item.name == "Comedy")
                            val bgBrush = if (isSelected) {
                                Brush.linearGradient(colors = listOf(PrimaryAccent.copy(alpha = 0.08f), SecondaryAccent.copy(alpha = 0.08f)))
                            } else {
                                Brush.linearGradient(colors = listOf(Color(0xFF18181B), Color(0xFF18181B)))
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgBrush)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryAccent else Color(0xFF27272A),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onNicheChanged(item.name) }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = "${item.name} Icon",
                                        tint = if (isSelected) PrimaryAccent else if (isLocked) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = item.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else if (isLocked) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        if (isLocked) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked Niche",
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(10.dp)
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

        // Topic / Concept Field Box
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Video Concept or Topic",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                letterSpacing = 0.5.sp
            )

            OutlinedTextField(
                value = topicInput,
                onValueChange = onTopicInputChanged,
                placeholder = {
                    Text(
                        text = "e.g., 3 productivity secrets that changed my morning routine...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("script_topic_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = Color(0xFF27272A),
                    focusedContainerColor = Color(0xFF121214),
                    unfocusedContainerColor = Color(0xFF121214)
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
            )
        }

        // Parameters Grid: Tone & Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tone Pillar
            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Target Tone",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tones) { tone ->
                        val isSelected = tone == selectedTone
                        val isLocked = !isPremium && (tone == "Dramatic" || tone == "Humorous")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryAccent else Color(0xFF18181B))
                                .border(1.dp, if (isSelected) PrimaryAccent else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                .clickable { onToneChanged(tone) }
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = tone,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (isLocked) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                if (isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Tone",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Duration Pillar
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Duration",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    durations.forEach { duration ->
                        val isSelected = duration == selectedDuration
                        val isLocked = !isPremium && duration == "60s"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SecondaryAccent else Color(0xFF18181B))
                                .border(1.dp, if (isSelected) SecondaryAccent else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                .clickable { onDurationChanged(duration) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = duration,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (isLocked) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                if (isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Duration",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Generate Action Button
        Button(
            onClick = onGenerateClick,
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_button"),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isGenerating) {
                            Brush.linearGradient(colors = listOf(Color(0xFF27272A), Color(0xFF27272A)))
                        } else {
                            Brush.linearGradient(colors = listOf(PrimaryAccent, SecondaryAccent))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.0.dp
                        )
                        Text(
                            text = "AI is Sequencing...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "wand sprinkles icon",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Generate AI Script",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Intro tagline
                Text(
                    text = "Pick an isolated AI generation toolkit to micro-craft viral elements on-demand:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )

                // Tool 0: AI Viral Hook Generator
                val isHookExpanded = expandedTool == 0
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (isHookExpanded) PrimaryAccent.copy(alpha = 0.5f) else Color(0xFF27272A), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121214))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { expandedTool = 0 },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Hook Tool Icon", tint = PrimaryAccent, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("AI Viral Hook Generator", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Generate 3 high retention scroll-stoppers", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                            Icon(
                                imageVector = if (isHookExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand Status",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        if (isHookExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFF27272A))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Hook Concept or Focus Topic", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = hookInputTopic,
                                onValueChange = onHookInputTopicChanged,
                                placeholder = { Text("e.g. why 99% of morning routines fail", fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth().testTag("hook_topic_input"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryAccent,
                                    unfocusedBorderColor = Color(0xFF27272A),
                                    focusedContainerColor = Color(0xFF18181C),
                                    unfocusedContainerColor = Color(0xFF18181C)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Viral Angle Style", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            val hookStylesList = listOf("Curiosity Gap", "Fear of Missing Out", "Contrarian Take", "Value Bait")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(hookStylesList) { style ->
                                    val isSelected = style == hookStyle
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryAccent else Color(0xFF18181C))
                                            .border(1.dp, if (isSelected) PrimaryAccent else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                            .clickable { onHookStyleChanged(style) }
                                            .padding(vertical = 6.dp, horizontal = 10.dp)
                                    ) {
                                        Text(style, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onGenerateHookClick,
                                enabled = !isGeneratingHook,
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("generate_hook_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                            ) {
                                if (isGeneratingHook) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sparking Viral Hooks...", fontSize = 12.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Wand Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate 3 Custom Hooks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (generatedHooks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("🔥 Scroll-Stoppers Recommended:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryAccent)
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    generatedHooks.forEachIndexed { index, individualHook ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181C))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("\"$individualHook\"", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 16.sp)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Copy Icon
                                                    TextButton(
                                                        onClick = {
                                                            clipboardManager.setText(AnnotatedString(individualHook))
                                                            onToastNeeded("Hook copied!")
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Copy Hook", modifier = Modifier.size(12.dp), tint = PrimaryAccent)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Copy", fontSize = 10.sp, color = PrimaryAccent)
                                                    }

                                                    if (generatedDraft != null) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Button(
                                                            onClick = { onApplyHookToDraft(individualHook) },
                                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent.copy(alpha = 0.15f), contentColor = PrimaryAccent)
                                                        ) {
                                                            Text("Apply to Draft", fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
                }

                // Tool 1: AI Instagram Caption Generator
                val isCaptionExpanded = expandedTool == 1
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (isCaptionExpanded) SecondaryAccent.copy(alpha = 0.5f) else Color(0xFF27272A), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121214))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { expandedTool = 1 },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Caption Tool Icon", tint = SecondaryAccent, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("AI Instagram Caption Writer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Write high conversion storytelling captions", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                            Icon(
                                imageVector = if (isCaptionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand Status",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        if (isCaptionExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFF27272A))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Key Points / Summary of Video", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = captionInputSummary,
                                onValueChange = onCaptionInputSummaryChanged,
                                placeholder = { Text("e.g. 3 productivity hacks, morning schedule, compound focus secrets", fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth().height(80.dp).testTag("caption_summary_input"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryAccent,
                                    unfocusedBorderColor = Color(0xFF27272A),
                                    focusedContainerColor = Color(0xFF18181C),
                                    unfocusedContainerColor = Color(0xFF18181C)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Formatting Tone Format", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            val formatsList = listOf("Storytelling", "Short & Punchy", "Step-by-Step tutorial", "Engaging Question")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(formatsList) { style ->
                                    val isSelected = style == captionFormatStyle
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) SecondaryAccent else Color(0xFF18181C))
                                            .border(1.dp, if (isSelected) SecondaryAccent else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                            .clickable { onCaptionFormatStyleChanged(style) }
                                            .padding(vertical = 6.dp, horizontal = 10.dp)
                                    ) {
                                        Text(style, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onGenerateCaptionClick,
                                enabled = !isGeneratingCaption,
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("generate_caption_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent)
                            ) {
                                if (isGeneratingCaption) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Writing Caption...", fontSize = 12.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Wand Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Instagram Caption", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (generatedCaption.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("📝 High-Converting Captions Result:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryAccent)
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181C))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(generatedCaption, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 17.sp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(generatedCaption))
                                                    onToastNeeded("Caption copied!")
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = "Copy Caption", modifier = Modifier.size(12.dp), tint = SecondaryAccent)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy Caption", fontSize = 10.sp, color = SecondaryAccent)
                                            }

                                            if (generatedDraft != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = { onApplyCaptionToDraft(generatedCaption) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent.copy(alpha = 0.15f), contentColor = SecondaryAccent),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Apply to Draft", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tool 2: Trending Hashtags Finder
                val isHashtagsExpanded = expandedTool == 2
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (isHashtagsExpanded) Color(0xFF06B6D4).copy(alpha = 0.5f) else Color(0xFF27272A), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121214))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { expandedTool = 2 },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.List, contentDescription = "Hashtag Tool Icon", tint = Color(0xFF06B6D4), modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Trending Hashtags Finder", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Generate optimized trending tags automatically", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                            Icon(
                                imageVector = if (isHashtagsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand Status",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        if (isHashtagsExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFF27272A))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Core Topic Keywords", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = hashtagInputTopic,
                                onValueChange = onHashtagInputTopicChanged,
                                placeholder = { Text("e.g. content creation workout productivity", fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth().testTag("hashtag_topic_input"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF06B6D4),
                                    unfocusedBorderColor = Color(0xFF27272A),
                                    focusedContainerColor = Color(0xFF18181C),
                                    unfocusedContainerColor = Color(0xFF18181C)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Target Social Platform", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            val platformsList = listOf("Instagram", "TikTok", "YouTube Shorts")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(platformsList) { pl ->
                                    val isSelected = pl == hashtagPlatform
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF06B6D4) else Color(0xFF18181C))
                                            .border(1.dp, if (isSelected) Color(0xFF06B6D4) else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                            .clickable { onHashtagPlatformChanged(pl) }
                                            .padding(vertical = 6.dp, horizontal = 12.dp)
                                    ) {
                                        Text(pl, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.6f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onGenerateHashtagsClick,
                                enabled = !isGeneratingHashtags,
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("generate_hashtags_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4), contentColor = Color.Black)
                            ) {
                                if (isGeneratingHashtags) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Finding Tags...", fontSize = 12.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Wand Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Viral Hashtags", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (generatedHashtags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("🏷️ Recommended Viral Hashtags:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181C))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(generatedHashtags, fontSize = 13.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(generatedHashtags))
                                                    onToastNeeded("Hashtags copied!")
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = "Copy Tags", modifier = Modifier.size(12.dp), tint = Color(0xFF06B6D4))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy Tags", fontSize = 10.sp, color = Color(0xFF06B6D4))
                                            }

                                            if (generatedDraft != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = { onApplyHashtagsToDraft(generatedHashtags) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4).copy(alpha = 0.15f), contentColor = Color(0xFF06B6D4)),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Apply to Draft", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tool 3: Thumbnail Text Overlay Designer
                val isThumbnailsExpanded = expandedTool == 3
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, if (isThumbnailsExpanded) Color(0xFFC084FC).copy(alpha = 0.5f) else Color(0xFF27272A), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121214))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { expandedTool = 3 },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Thumbnail Tool Icon", tint = Color(0xFFC084FC), modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Thumbnail Text Overlay Designer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Generate 3 high impact click-intent frames", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                            Icon(
                                imageVector = if (isThumbnailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand Status",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        if (isThumbnailsExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFF27272A))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Core Message of Reel", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = thumbnailInputConcept,
                                onValueChange = onThumbnailInputConceptChanged,
                                placeholder = { Text("e.g. stop losing compound micro-investments", fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth().testTag("thumbnail_concept_input"),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFC084FC),
                                    unfocusedBorderColor = Color(0xFF27272A),
                                    focusedContainerColor = Color(0xFF18181C),
                                    unfocusedContainerColor = Color(0xFF18181C)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Click-Intent Tactic", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            val tacticsList = listOf("Extreme Disbelief", "Curiosity Gap", "Result Solutions")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(tacticsList) { tactic ->
                                    val isSelected = tactic == thumbnailTactic
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFFC084FC) else Color(0xFF18181C))
                                            .border(1.dp, if (isSelected) Color(0xFFC084FC) else Color(0xFF27272A), RoundedCornerShape(8.dp))
                                            .clickable { onThumbnailTacticChanged(tactic) }
                                            .padding(vertical = 6.dp, horizontal = 12.dp)
                                    ) {
                                        Text(tactic, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.6f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onGenerateThumbnailsClick,
                                enabled = !isGeneratingThumbnails,
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("generate_thumbnails_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC), contentColor = Color.Black)
                            ) {
                                if (isGeneratingThumbnails) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Designing Words...", fontSize = 12.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Wand Icon", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Thumbnail Overlays", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (generatedThumbnails.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("🖼️ Designed Headline Overlays:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC084FC))
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    generatedThumbnails.forEach { txt ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFC084FC).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181C))
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Yellow Mock Visual Text Overlay
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color.Yellow.copy(alpha = 0.1f))
                                                        .border(1.dp, Color.Yellow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                                ) {
                                                    Text(
                                                        text = txt.uppercase(),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.Yellow,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    IconButton(
                                                        onClick = {
                                                            clipboardManager.setText(AnnotatedString(txt))
                                                            onToastNeeded("Overlay text copied!")
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Copy Overlay", tint = Color(0xFFC084FC), modifier = Modifier.size(14.dp))
                                                    }

                                                    if (generatedDraft != null) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Button(
                                                            onClick = { onApplyThumbnailTextToDraft(txt) },
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC084FC).copy(alpha = 0.15f), contentColor = Color(0xFFC084FC)),
                                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("Apply", fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
                }
            }
        }

        // LOADING OVERLAY SIMULATOR
        AnimatedVisibility(
            visible = isGenerating,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF18181B)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = PrimaryAccent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Structuring dialogue hook pipeline...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Injecting niche triggers & calls to action",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        }

        // SCRIPT OUTPUT WINDOW
        AnimatedVisibility(
            visible = generatedDraft != null,
            enter = fadeIn()
        ) {
            if (generatedDraft != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Generated Script Preview",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Copy button (all package items together)
                            IconButton(
                                onClick = {
                                    val fullScript = """
                                        === ${generatedDraft.title} ===
                                        [THUMBNAIL TEXT OVERLAY] 🖼️:
                                        ${generatedDraft.thumbnailText}
                                        
                                        [HOOK] 🧲 (0:00 - 0:05):
                                        ${generatedDraft.hook}
                                        
                                        [SCENE-BY-SCENE SCRIPT] 🎬 (0:05 - 0:25):
                                        ${generatedDraft.body}
                                        
                                        [CTA] 📣 (0:25 - 0:30):
                                        ${generatedDraft.callToAction}
                                        
                                        [INSTAGRAM CAPTION] 📝:
                                        ${generatedDraft.caption}
                                        
                                        [HASHTAGS] 🏷️:
                                        ${generatedDraft.hashtags}
                                    """.trimIndent()
                                    clipboardManager.setText(AnnotatedString(fullScript))
                                    onToastNeeded("Complete bundle copied to clipboard!")
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF18181B))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Copy full script package",
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Save button
                            IconButton(
                                onClick = { onSaveDraftClick(generatedDraft) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryAccent)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save draft action",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Structured Script Output Card matches premium designs
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF121214))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Header Meta tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PrimaryAccent.copy(alpha = 0.12f))
                                        .padding(vertical = 3.dp, horizontal = 9.dp)
                                ) {
                                    Text(
                                        text = "${generatedDraft.niche.uppercase()} • VIRAL REEL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryAccent,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "⏳ ${generatedDraft.duration} (${generatedDraft.tone.uppercase()})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color(0xFF27272A))

                            // THUMBNAIL TEXT OVERLAY SEGMENT (Simulation/Mockup Card)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "THUMBNAIL TEXT OVERLAY 🖼️",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC084FC),
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.thumbnailText))
                                            onToastNeeded("Thumbnail text copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy thumbnail text",
                                            tint = Color(0xFFC084FC).copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(75.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.linearGradient(colors = listOf(Color(0xFF3B0764), Color(0xFF1E1B4B)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = generatedDraft.thumbnailText.uppercase(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Yellow,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Reel Thumbnail Hook Mock",
                                            fontSize = 9.sp,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }

                            // Hook Segment
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "THE VIRAL HOOK 🧲 [0:00 - 0:05]",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryAccent,
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.hook))
                                            onToastNeeded("Viral hook copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy hook",
                                            tint = PrimaryAccent.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "\"${generatedDraft.hook}\"",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )
                            }

                            // Body Segment (Scene-by-scene script)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SCENE-BY-SCENE SCRIPT 🎬 [0:05 - 0:25]",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryAccent,
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.body))
                                            onToastNeeded("Scene-by-scene script copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy script body",
                                            tint = SecondaryAccent.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = generatedDraft.body,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                            }

                            // CTA Segment
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CALL TO ACTION 📣 [0:25 - 0:30]",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.callToAction))
                                            onToastNeeded("CTA copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy cta",
                                            tint = Color(0xFF10B981).copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "\"${generatedDraft.callToAction}\"",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )
                            }

                            HorizontalDivider(color = Color(0xFF27272A))

                            // INSTAGRAM CAPTION SEGMENT
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "VIRAL POST CAPTION 📝",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B),
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.caption))
                                            onToastNeeded("Post caption copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy caption",
                                            tint = Color(0xFFF59E0B).copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18181C))
                                ) {
                                    Text(
                                        text = generatedDraft.caption,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            // HASHTAGS SEGMENT
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "VIRAL HASHTAGS 🏷️",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF06B6D4),
                                        letterSpacing = 0.5.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDraft.hashtags))
                                            onToastNeeded("Hashtags copied!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy hashtags",
                                            tint = Color(0xFF06B6D4).copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = generatedDraft.hashtags,
                                    fontSize = 12.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Safety Warning block conforming to Secret Management and security policies
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFDC2626).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF15080A)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Warning shield icon",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Security Warning",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
                Text(
                    text = "I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

private data class NicheItem(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
