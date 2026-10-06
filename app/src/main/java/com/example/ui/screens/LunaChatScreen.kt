package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.ui.LunaViewModel
import com.example.ui.components.AssistantOrbState
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.LunaAmberTertiary
import com.example.ui.theme.LunaBearishRed
import com.example.ui.theme.LunaBullishGreen
import com.example.ui.theme.LunaCyanPrimary
import com.example.ui.theme.LunaDarkSurface
import com.example.ui.theme.LunaDarkSurfaceVariant
import com.example.ui.theme.LunaPurpleSecondary
import com.example.ui.theme.LunaTextPrimary
import com.example.ui.theme.LunaTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LunaChatScreen(
    viewModel: LunaViewModel,
    modifier: Modifier = Modifier
) {
    val orbState by viewModel.orbState.collectAsState()
    val statusText by viewModel.statusTextHi.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val liveTranscription by viewModel.voiceManager.liveTranscription.collectAsState()
    val audioRms by viewModel.voiceManager.audioLevelRms.collectAsState()
    val pendingAction by viewModel.pendingAction.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "लूना, अरिजीत सिंह का गाना चलाओ",
        "WhatsApp पर मैसेज लिखो कि मैं 5 मिनट में पहुँच रहा हूँ",
        "ICT में Liquidity Sweep समझाओ",
        "Fair Value Gap (FVG) क्या है?",
        "कल सुबह 6 बजे का अलार्म लगाओ",
        "YouTube पर ICT ट्रेडिंग ट्यूटोरियल खोजो",
        "BOS बनाम CHOCH समझाओ",
        "लूना, चुप हो जाओ"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pending Action Confirmation Card
        AnimatedVisibility(visible = pendingAction != null) {
            pendingAction?.let { action ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(LunaCyanPrimary, LunaPurpleSecondary)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "पुष्टि की आवश्यकता (Confirmation)",
                            style = MaterialTheme.typography.labelLarge,
                            color = LunaAmberTertiary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.descriptionHi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LunaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.dismissPendingAction() },
                                modifier = Modifier.testTag("action_cancel_button")
                            ) {
                                Text("रद्द करें")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.confirmPendingAction() },
                                colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                                modifier = Modifier.testTag("action_confirm_button")
                            ) {
                                Text("अनुमति दें (भेजें)", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Top Section: Voice Orb and Live Assistant Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Wake word and state chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "वेक वर्ड: “लूना” / “Hey Luna”",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (isSpeaking) {
                        Surface(
                            color = LunaBearishRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "बोल रही है",
                                    tint = LunaBearishRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "बोल रही है",
                                    color = LunaBearishRed,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isListening) {
                        Surface(
                            color = LunaBullishGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "सुन रही है…",
                                color = LunaBullishGreen,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The Central Glowing Celestial Orb
                VoiceOrb(
                    state = orbState,
                    audioRms = audioRms,
                    size = 130.dp,
                    onClick = { viewModel.toggleVoiceListening() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status text / Live speech
                Text(
                    text = if (isListening && liveTranscription.isNotBlank()) "आप बोल रहे हैं: \"$liveTranscription\"" else statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isListening) LunaCyanPrimary else LunaTextPrimary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Controls Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.toggleVoiceListening() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) LunaBearishRed else LunaCyanPrimary
                        ),
                        modifier = Modifier.testTag("voice_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "माइक",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "रोकें (Stop)" else "माइक से बोलें",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSpeaking) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.stopSpeaking() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LunaAmberTertiary),
                            modifier = Modifier.testTag("stop_speaking_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "चुप हो जाओ",
                                tint = LunaAmberTertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("चुप हो जाओ")
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips Horizontal Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                SuggestionChip(
                    onClick = { viewModel.sendTextQuery(prompt) },
                    label = { Text(prompt, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = LunaDarkSurfaceVariant,
                        labelColor = LunaTextSecondary
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color(0xFF2E3B5B))
                )
            }
        }

        // Conversation History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatMessages, key = { it.id }) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Bottom Input Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleVoiceListening() },
                    modifier = Modifier.testTag("bottom_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "माइक",
                        tint = if (isListening) LunaCyanPrimary else LunaTextSecondary
                    )
                }

                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("query_input_field"),
                    placeholder = {
                        Text(
                            "हिंदी या Hinglish में आदेश लिखें…",
                            style = MaterialTheme.typography.bodySmall,
                            color = LunaTextSecondary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = LunaTextPrimary,
                        unfocusedTextColor = LunaTextPrimary
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputPrompt.isNotBlank()) {
                            viewModel.sendTextQuery(inputPrompt)
                            inputPrompt = ""
                        }
                    })
                )

                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank()) {
                            viewModel.sendTextQuery(inputPrompt)
                            inputPrompt = ""
                        }
                    },
                    enabled = inputPrompt.isNotBlank(),
                    modifier = Modifier.testTag("send_query_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "भेजें",
                        tint = if (inputPrompt.isNotBlank()) LunaCyanPrimary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity) {
    val isUser = message.sender == "USER"
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(LunaCyanPrimary, LunaPurpleSecondary))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "L",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) LunaCyanPrimary.copy(alpha = 0.15f) else LunaDarkSurfaceVariant
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = if (isUser) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(LunaCyanPrimary, LunaCyanPrimary.copy(alpha = 0.4f)))) else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "आप (You)" else "LUNA",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) LunaCyanPrimary else LunaPurpleSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LunaTextPrimary,
                    lineHeight = 22.sp
                )

                if (message.actionType != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = LunaDarkSurface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "एक्शन: ${message.actionType}",
                            color = LunaAmberTertiary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = LunaTextSecondary,
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 10.sp
                )
            }
        }
    }
}
