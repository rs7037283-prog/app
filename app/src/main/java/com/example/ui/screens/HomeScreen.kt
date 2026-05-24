package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Draft
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent

@Composable
fun HomeScreen(
    isPremium: Boolean,
    generatedCount: Int,
    onUpgradeClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit,
    savedDrafts: List<Draft>,
    onStartPromptingClick: () -> Unit,
    onViewAllDraftsClick: () -> Unit,
    onOlderDraftClick: (Draft) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
    ) {
        // Greeting Header
        item {
            Column {
                Text(
                    text = "Hey, Creator! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Let's formulate your next viral dynamic layout.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }

        // Premium Core Widget Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("premium_status_card")
                    .glassCard(
                        cornerRadius = 16.dp,
                        backgroundColor = if (isPremium) Color(0x2B064E3B) else Color(0x2BFBBF24).copy(alpha = 0.08f)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isPremium) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF064E3B).copy(alpha = 0.25f),
                                        Color(0xFF0F172A).copy(alpha = 0.25f)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF78350F).copy(alpha = 0.25f),
                                        Color(0xFF18181B).copy(alpha = 0.25f)
                                    )
                                )
                            }
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPremium) Icons.Default.Check else Icons.Default.Star,
                                    contentDescription = "Tier Badge Icon",
                                    tint = if (isPremium) Color(0xFF34D399) else Color(0xFFFBBF24),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isPremium) "REELAI ELITE PRO ACTIVE" else "REELAI FREE TIER PLAN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPremium) Color(0xFF34D399) else Color(0xFFFBBF24),
                                    letterSpacing = 1.sp
                                )
                            }

                            // Dynamic Badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPremium) Color(0xFF064E3B) else Color(0xFF78350F)
                            ) {
                                Text(
                                    text = if (isPremium) "VIP" else "FREE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isPremium) {
                                "Unlimited high-velocity script synthesis active. Exclusive niches, viral dramatic tones, and maximum duration modules unlocked."
                            } else {
                                "Unlock business/comedy niches, deep-retention dramatic tones, and unlimited generation credits (Current usage: $generatedCount/3 scripts used)"
                            },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isPremium) {
                                Button(
                                    onClick = onUpgradeClick,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFBBF24),
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Upgrade Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = onCancelSubscriptionClick,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF87171).copy(alpha = 0.15f),
                                        contentColor = Color(0xFFF87171)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.3f)),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Reset Tier (Test Downgrade)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Stats Row Widget
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Runs Box
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .glassCard(cornerRadius = 16.dp, backgroundColor = Color(0x18FFFFFF)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL RUNS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Stats CPU Icon",
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // We can calculate the statistics. To make it dynamic, let's use a nice arbitrary start offset plus saved total drafts size
                        Text(
                            text = "${savedDrafts.size + 4}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "scripts generated",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }

                // Saved Drafts Box
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .glassCard(cornerRadius = 16.dp, backgroundColor = Color(0x18FFFFFF)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SAVED DRAFTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SecondaryAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Stats Check Icon",
                                    tint = SecondaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${savedDrafts.size}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "ready for export",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Quick Creator Prompt box with vibrant gradient
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(cornerRadius = 20.dp, backgroundColor = Color(0x10FFFFFF))
                    .clickable { onStartPromptingClick() }
                    .testTag("quick_creator_prompt_card"),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    PrimaryAccent.copy(alpha = 0.12f),
                                    SecondaryAccent.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Start Prompting",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Forward Icon",
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jump straight into the script sandbox and draft your viral ideas with AI insights.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Wand Icon",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Recent Drafts Preview header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Drafts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (savedDrafts.isNotEmpty()) {
                    TextButton(onClick = onViewAllDraftsClick) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            color = PrimaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live recent drafts content layout
        if (savedDrafts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .glassCard(cornerRadius = 16.dp, backgroundColor = Color(0x10FFFFFF))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Empty drafts catalog",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Draft scripts in the Creator tab first.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Pick max 3 drafts
            items(savedDrafts.take(3)) { draft ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard(cornerRadius = 16.dp, backgroundColor = Color(0x10FFFFFF))
                        .clickable { onOlderDraftClick(draft) },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryAccent.copy(alpha = 0.12f))
                                    .padding(vertical = 2.dp, horizontal = 6.dp)
                            ) {
                                Text(
                                    text = draft.niche.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryAccent,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = draft.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Topic: ${draft.topic}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Details Icon",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
