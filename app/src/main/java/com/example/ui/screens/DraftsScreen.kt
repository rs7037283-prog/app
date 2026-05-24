package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Draft
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent

@Composable
fun DraftsScreen(
    savedDrafts: List<Draft>,
    onDeleteDraftClick: (Draft) -> Unit,
    onToastNeeded: (String) -> Unit
) {
    var expandedDraftId by remember { mutableStateOf<Int?>(null) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("drafts_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title Header
        Column {
            Text(
                text = "Your Drafts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Quickly view, copy, or delete saved scripts",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        if (savedDrafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No Drafts Icon",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved drafts yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Go to the Script Studio to generate and save your customized scripts.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(savedDrafts) { draft ->
                    val isExpanded = expandedDraftId == draft.id
                    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassCard(cornerRadius = 16.dp, backgroundColor = Color(0x10FFFFFF)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Header segment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PrimaryAccent.copy(alpha = 0.12f))
                                        .padding(vertical = 2.dp, horizontal = 7.dp)
                                ) {
                                    Text(
                                        text = draft.niche.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryAccent
                                    )
                                }

                                Text(
                                    text = "⏳ ${draft.duration}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }

                            // Title & Concept description
                            Text(
                                text = draft.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Topic: ${draft.topic}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            // Collapsible content detail view
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    HorizontalDivider(color = Color(0xFF27272A))

                                    // Thumbnail text
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "THUMBNAIL OVERLAY 🖼️",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC084FC)
                                        )
                                        Text(
                                            text = draft.thumbnailText.ifBlank { "N/A" },
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Hook
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "HOOK 🧲",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryAccent
                                        )
                                        Text(
                                            text = "\"${draft.hook}\"",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }

                                    // Body
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "SCENE-BY-SCENE SCRIPT 🎬",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecondaryAccent
                                        )
                                        Text(
                                            text = draft.body,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    // CTA
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "CTA 📣",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = "\"${draft.callToAction}\"",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }

                                    // Caption
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "POST CAPTION 📝",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF59E0B)
                                        )
                                        Text(
                                            text = draft.caption.ifBlank { "N/A" },
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 15.sp
                                        )
                                    }

                                    // Hashtags
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "HASHTAGS 🏷️",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF06B6D4)
                                        )
                                        Text(
                                            text = draft.hashtags.ifBlank { "N/A" },
                                            fontSize = 11.sp,
                                            color = Color(0xFF38BDF8),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFF1C1C1E))

                            // Bottom actions panel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Toggle expand/collapse
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            expandedDraftId = if (isExpanded) null else draft.id
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isExpanded) "Collapse" else "Expand Details",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryAccent
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand/Collapse Icon",
                                        tint = PrimaryAccent,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .rotate(rotation)
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Copy layout button
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Copy script button",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                val fullScript = """
                                                    === ${draft.title} ===
                                                    [THUMBNAIL TEXT OVERLAY] 🖼️:
                                                    ${draft.thumbnailText}
                                                    
                                                    [HOOK] 🧲 (0:00 - 0:05):
                                                    ${draft.hook}
                                                    
                                                    [SCENE-BY-SCENE SCRIPT] 🎬 (0:05 - 0:25):
                                                    ${draft.body}
                                                    
                                                    [CTA] 📣 (0:25 - 0:30):
                                                    ${draft.callToAction}
                                                    
                                                    [INSTAGRAM CAPTION] 📝:
                                                    ${draft.caption}
                                                    
                                                    [HASHTAGS] 🏷️:
                                                    ${draft.hashtags}
                                                """.trimIndent()
                                                clipboardManager.setText(AnnotatedString(fullScript))
                                                onToastNeeded("Copied entire script package!")
                                            }
                                    )

                                    // Delete draft button
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete draft button",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { onDeleteDraftClick(draft) }
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
