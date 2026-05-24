package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.SecondaryAccent

@Composable
fun AuthScreen(
    isAuthLoading: Boolean,
    isFirebaseActive: Boolean,
    onLoginClick: (String, String, Boolean) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current

    GlassmorphicBackground(
        modifier = Modifier.testTag("auth_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(cornerRadius = 24.dp, backgroundColor = Color(0x38101015))
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Image/Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryAccent.copy(alpha = 0.2f), SecondaryAccent.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Reel Header Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Cloud Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isFirebaseActive) Color(0xFF0F5132).copy(alpha = 0.6f) else Color(0xFF2C1A04).copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (isFirebaseActive) Color(0x3310B981) else Color(0x33F59E0B), RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isFirebaseActive) Color.Green else Color.Yellow)
                        )
                        Text(
                            text = if (isFirebaseActive) "LIVE FIREBASE CLOUD ACTIVE" else "LOCAL SANDBOX OFFLINE MODE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirebaseActive) Color(0xFFD1E7DD) else Color(0xFFFFF3CD),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isSignUp) "Create Account" else "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isSignUp) "Join the next generation of creative builders" else "Sign in to start creating viral scripts",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isAuthLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = PrimaryAccent,
                        trackColor = Color(0x1AFFFFFF)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Email Address Input field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("creator@reelai.com") },
                    enabled = !isAuthLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedLabelColor = PrimaryAccent,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0x1FFFFFFF),
                        unfocusedContainerColor = Color(0x0CFFFFFF),
                        disabledTextColor = Color.Gray,
                        disabledBorderColor = Color(0xFF1C1C1E)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    enabled = !isAuthLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedLabelColor = PrimaryAccent,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0x1FFFFFFF),
                        unfocusedContainerColor = Color(0x0CFFFFFF),
                        disabledTextColor = Color.Gray,
                        disabledBorderColor = Color(0xFF1C1C1E)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (!isAuthLoading && email.isNotBlank() && password.isNotBlank()) {
                                onLoginClick(email, password, isSignUp)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // CTA Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onLoginClick(email, password, isSignUp)
                    },
                    enabled = !isAuthLoading && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sign_in_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFF1A1A1E)
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isAuthLoading || email.isBlank() || password.isBlank()) {
                                    Brush.linearGradient(colors = listOf(Color(0xFF27272A), Color(0xFF18181B)))
                                } else {
                                    Brush.linearGradient(colors = listOf(PrimaryAccent, SecondaryAccent))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSignUp) "Sign Up" else "Sign In",
                            color = if (isAuthLoading || email.isBlank() || password.isBlank()) Color.Gray else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Switch sign in mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = if (isSignUp) "Sign In" else "Create Account",
                        fontSize = 12.sp,
                        color = if (isAuthLoading) Color.Gray else PrimaryAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(enabled = !isAuthLoading) { isSignUp = !isSignUp }
                            .testTag("toggle_mode_btn")
                    )
                }
            }
        }
    }
}
