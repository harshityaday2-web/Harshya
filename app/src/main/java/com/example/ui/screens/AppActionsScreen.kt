package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actions.AppActionExecutor
import com.example.ui.LunaViewModel
import com.example.ui.theme.LunaAmberTertiary
import com.example.ui.theme.LunaCyanPrimary
import com.example.ui.theme.LunaDarkSurface
import com.example.ui.theme.LunaDarkSurfaceVariant
import com.example.ui.theme.LunaPurpleSecondary
import com.example.ui.theme.LunaTextPrimary
import com.example.ui.theme.LunaTextSecondary

@Composable
fun AppActionsScreen(
    viewModel: LunaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var whatsappPhone by remember { mutableStateOf("") }
    var whatsappMessage by remember { mutableStateOf("नमस्ते, मैं LUNA AI के माध्यम से संदेश भेज रहा हूँ।") }
    var youtubeQuery by remember { mutableStateOf("Arijit Singh Best Songs") }
    var alarmHour by remember { mutableStateOf("06") }
    var alarmMinute by remember { mutableStateOf("00") }
    var alarmLabel by remember { mutableStateOf("सुबह का अलार्म") }
    var timerMinutes by remember { mutableStateOf("5") }
    var searchQuery by remember { mutableStateOf("आज की ताज़ा ख़बरें") }

    var actionFeedback by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "मोबाइल ऐप नियंत्रण और स्मार्ट क्रियाएँ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LunaCyanPrimary
                )
                Text(
                    text = "Android के आधिकारिक और सुरक्षित तरीकों से अन्य ऐप्स चलाएं",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary
                )
            }
        }

        if (actionFeedback.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = actionFeedback,
                        color = LunaAmberTertiary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // WhatsApp Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WhatsApp मैसेज इंटीग्रेशन",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366)
                        )
                    }

                    Text(
                        text = "फोन नंबर वैकल्पिक है; यदि खाली रहेगा तो संदेश WhatsApp में ड्राफ्ट हो जाएगा ताकि आप किसी भी चैट को चुन सकें।",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextSecondary
                    )

                    OutlinedTextField(
                        value = whatsappPhone,
                        onValueChange = { whatsappPhone = it },
                        label = { Text("फ़ोन नंबर (देश कोड सहित, उदा. +91...)") },
                        modifier = Modifier.fillMaxWidth().testTag("whatsapp_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = whatsappMessage,
                        onValueChange = { whatsappMessage = it },
                        label = { Text("मैसेज टेक्स्ट") },
                        modifier = Modifier.fillMaxWidth().testTag("whatsapp_msg_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            val res = AppActionExecutor.openWhatsApp(context, whatsappPhone, whatsappMessage)
                            actionFeedback = res.messageInHindi
                            viewModel.voiceManager.speak(res.messageInHindi)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier.fillMaxWidth().testTag("whatsapp_send_button")
                    ) {
                        Text("WhatsApp में संदेश खोलें (Open in WhatsApp)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // YouTube & Music Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = "YouTube", tint = Color(0xFFFF0000))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "YouTube एवं म्यूजिक प्लेयर",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF0000)
                        )
                    }

                    OutlinedTextField(
                        value = youtubeQuery,
                        onValueChange = { youtubeQuery = it },
                        label = { Text("वीडियो या गाने का नाम") },
                        modifier = Modifier.fillMaxWidth().testTag("youtube_query_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            val res = AppActionExecutor.openYouTube(context, youtubeQuery)
                            actionFeedback = res.messageInHindi
                            viewModel.voiceManager.speak(res.messageInHindi)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        modifier = Modifier.fillMaxWidth().testTag("youtube_search_button")
                    ) {
                        Text("YouTube पर खोजें और चलाएं", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Media Controls
                    Text(
                        text = "सिस्टम मीडिया कंट्रोल्स:",
                        style = MaterialTheme.typography.labelSmall,
                        color = LunaTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val res = AppActionExecutor.controlMedia(context, "pause")
                                actionFeedback = res.messageInHindi
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LunaDarkSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Pause", tint = LunaAmberTertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("रोकें (Pause)", fontSize = 11.sp, color = LunaTextPrimary)
                        }

                        Button(
                            onClick = {
                                val res = AppActionExecutor.controlMedia(context, "play")
                                actionFeedback = res.messageInHindi
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LunaDarkSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = LunaCyanPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("चलाएं (Play)", fontSize = 11.sp, color = LunaTextPrimary)
                        }

                        Button(
                            onClick = {
                                val res = AppActionExecutor.controlMedia(context, "next")
                                actionFeedback = res.messageInHindi
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LunaDarkSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = LunaPurpleSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("अगला (Next)", fontSize = 11.sp, color = LunaTextPrimary)
                        }
                    }
                }
            }
        }

        // Alarms and Timers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Alarm, contentDescription = "Alarm", tint = LunaCyanPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "अलार्म एवं टाइमर",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaCyanPrimary
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = alarmHour,
                            onValueChange = { alarmHour = it },
                            label = { Text("घंटे (Hour)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = alarmMinute,
                            onValueChange = { alarmMinute = it },
                            label = { Text("मिनट (Min)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val h = alarmHour.toIntOrNull() ?: 6
                            val m = alarmMinute.toIntOrNull() ?: 0
                            val res = AppActionExecutor.setAlarm(context, h, m, alarmLabel)
                            actionFeedback = res.messageInHindi
                            viewModel.voiceManager.speak(res.messageInHindi)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("set_alarm_button")
                    ) {
                        Text("अलार्म सेट करें", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = timerMinutes,
                            onValueChange = { timerMinutes = it },
                            label = { Text("टाइमर (मिनट)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val mins = timerMinutes.toIntOrNull() ?: 5
                                val res = AppActionExecutor.setTimer(context, mins * 60, "Luna Timer")
                                actionFeedback = res.messageInHindi
                                viewModel.voiceManager.speak(res.messageInHindi)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LunaPurpleSecondary),
                            modifier = Modifier.testTag("set_timer_button")
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = "Timer", tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("टाइमर शुरू", color = Color.Black)
                        }
                    }
                }
            }
        }

        // Web Search & Share
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = LunaAmberTertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "इंटरनेट वेब सर्च",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaAmberTertiary
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("सर्च क्वेरी") },
                        modifier = Modifier.fillMaxWidth().testTag("web_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            val res = AppActionExecutor.openWebSearch(context, searchQuery)
                            actionFeedback = res.messageInHindi
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaAmberTertiary),
                        modifier = Modifier.fillMaxWidth().testTag("web_search_button")
                    ) {
                        Text("ब्राउज़र में खोजें (Search Google)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
