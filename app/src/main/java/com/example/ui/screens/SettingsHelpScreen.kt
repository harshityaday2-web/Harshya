package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.ai.GeminiApiClient
import com.example.ui.LunaViewModel
import com.example.ui.theme.LunaAmberTertiary
import com.example.ui.theme.LunaBearishRed
import com.example.ui.theme.LunaBullishGreen
import com.example.ui.theme.LunaCyanPrimary
import com.example.ui.theme.LunaDarkSurface
import com.example.ui.theme.LunaDarkSurfaceVariant
import com.example.ui.theme.LunaPurpleSecondary
import com.example.ui.theme.LunaTextPrimary
import com.example.ui.theme.LunaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsHelpScreen(
    viewModel: LunaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isServiceActive by viewModel.isForegroundServiceActive.collectAsState()

    var speechPitch by remember { mutableFloatStateOf(1.05f) }
    var speechRate by remember { mutableFloatStateOf(0.95f) }

    var geminiTestResult by remember { mutableStateOf("") }
    var isTestingGemini by remember { mutableStateOf(false) }

    val hasAudioPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

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
                    text = "LUNA सेटिंग्स, सुरक्षा और सेटअप गाइड",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LunaCyanPrimary
                )
                Text(
                    text = "वास्तविक Android अनुमतियाँ, AI बैकएंड एवं वॉइस कॉन्फ़िगरेशन",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary
                )
            }
        }

        // Foreground Service & Wake Word Activation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "बैकग्राउंड वेक-वर्ड सेवा (Foreground Service)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LunaCyanPrimary
                            )
                            Text(
                                text = "ऐप से बाहर होने पर भी 'Hey Luna' सुनने के लिए सक्रिय करें।",
                                style = MaterialTheme.typography.bodySmall,
                                color = LunaTextSecondary
                            )
                        }

                        Switch(
                            checked = isServiceActive,
                            onCheckedChange = { viewModel.toggleForegroundService() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LunaCyanPrimary,
                                checkedTrackColor = LunaDarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("bg_service_switch")
                        )
                    }

                    Surface(
                        color = LunaDarkSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Android सीमा",
                                tint = LunaAmberTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "स्पष्ट तकनीकी सीमा: Android OS सुरक्षा के कारण यदि फोन पूरी तरह स्विच ऑफ हो या डीप स्लीप में हो, तो कोई भी थर्ड-पार्टी ऐप माइक्रोफोन नहीं सुन सकता। स्क्रीन लॉक होने पर यह सेवा स्थायी नोटिफिकेशन के साथ सर्वश्रेष्ठ संभव प्रदर्शन देती है।",
                                style = MaterialTheme.typography.bodySmall,
                                color = LunaTextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Gemini AI Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = "API", tint = LunaPurpleSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini AI बैकएंड स्थिति",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaPurpleSecondary
                        )
                    }

                    val isKeyConfigured = try {
                        val k = BuildConfig.GEMINI_API_KEY
                        k.isNotBlank() && k != "MY_GEMINI_API_KEY"
                    } catch (e: Exception) {
                        false
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gemini API Key:", color = LunaTextSecondary, style = MaterialTheme.typography.bodySmall)
                        Surface(
                            color = if (isKeyConfigured) LunaBullishGreen.copy(alpha = 0.2f) else LunaAmberTertiary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isKeyConfigured) "सक्रिय (Connected)" else "AI Studio Secrets में सेट करें",
                                color = if (isKeyConfigured) LunaBullishGreen else LunaAmberTertiary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "सुरक्षा नियम: आपकी API Key कभी भी ऐप कोड में हार्डकोड नहीं होती; यह AI Studio Secrets पैनल से सुरक्षित रूप से लोड होती है। यदि Key नहीं भी है, तो LUNA का इन-बिल्ट स्मार्ट ऑफलाइन इंजन सभी मुख्य टास्क चलाता है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextSecondary,
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = {
                            isTestingGemini = true
                            coroutineScope.launch {
                                val result = GeminiApiClient.processQuery("नमस्ते लूना, अपना संक्षिप्त परिचय दो")
                                geminiTestResult = result.displayResponseHi
                                isTestingGemini = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaPurpleSecondary),
                        modifier = Modifier.fillMaxWidth().testTag("test_gemini_button")
                    ) {
                        Text(
                            text = if (isTestingGemini) "जाँच की जा रही है…" else "AI बैकएंड कनेक्शन टेस्ट करें",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (geminiTestResult.isNotBlank()) {
                        Surface(
                            color = LunaDarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "टेस्ट रिस्पांस: $geminiTestResult",
                                color = LunaCyanPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Voice TTS Tuning
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Headphones, contentDescription = "Voice", tint = LunaCyanPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "हिंदी वॉइस टोन और गति (TTS Settings)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaCyanPrimary
                        )
                    }

                    Text("पिच (Pitch): ${String.format("%.2f", speechPitch)}x", style = MaterialTheme.typography.bodySmall, color = LunaTextSecondary)
                    Slider(
                        value = speechPitch,
                        onValueChange = {
                            speechPitch = it
                            viewModel.voiceManager.setPitch(it)
                        },
                        valueRange = 0.7f..1.4f,
                        colors = SliderDefaults.colors(thumbColor = LunaCyanPrimary, activeTrackColor = LunaCyanPrimary)
                    )

                    Text("बोलने की गति (Speech Rate): ${String.format("%.2f", speechRate)}x", style = MaterialTheme.typography.bodySmall, color = LunaTextSecondary)
                    Slider(
                        value = speechRate,
                        onValueChange = {
                            speechRate = it
                            viewModel.voiceManager.setSpeechRate(it)
                        },
                        valueRange = 0.7f..1.3f,
                        colors = SliderDefaults.colors(thumbColor = LunaCyanPrimary, activeTrackColor = LunaCyanPrimary)
                    )

                    Button(
                        onClick = {
                            viewModel.voiceManager.speak("नमस्ते! मैं आपकी पर्सनल हिंदी असिस्टेंट LUNA हूँ। मेरी आवाज़ अब कैसी लग रही है?")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("test_voice_button")
                    ) {
                        Text("वॉइस का नमूना सुनें (Test Hindi Voice)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Permissions Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = "Permissions", tint = LunaBullishGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "डिवाइस अनुमतियाँ (Android Permissions)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaBullishGreen
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("माइक्रोफ़ोन (RECORD_AUDIO)", style = MaterialTheme.typography.bodySmall, color = LunaTextPrimary)
                        Icon(
                            imageVector = if (hasAudioPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = "Mic Status",
                            tint = if (hasAudioPermission) LunaBullishGreen else LunaBearishRed
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("नोटिफ़िकेशन (POST_NOTIFICATIONS)", style = MaterialTheme.typography.bodySmall, color = LunaTextPrimary)
                        Icon(
                            imageVector = if (hasNotificationPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = "Notification Status",
                            tint = if (hasNotificationPermission) LunaBullishGreen else LunaAmberTertiary
                        )
                    }
                }
            }
        }

        // Full Voice Command Guide
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "उपयोगी हिंदी वॉइस कमांड सूची",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LunaAmberTertiary
                    )

                    val commands = listOf(
                        "• \"लूना, अरिजीत सिंह का गाना चलाओ\" -> YouTube पर गाना प्ले होगा",
                        "• \"लूना, WhatsApp पर मैसेज लिखो कि मैं आ रहा हूँ\" -> संदेश तैयार होगा",
                        "• \"लूना, ICT में FVG क्या होता है?\" -> हिंदी में ऑडियो और टेक्स्ट व्याख्या",
                        "• \"लूना, सुबह 7 बजे का अलार्म लगाओ\" -> सिस्टम अलार्म सेट होगा",
                        "• \"लूना, 5 मिनट का टाइमर लगाओ\" -> टाइमर सेट होगा",
                        "• \"लूना, गाना रोको / अगला गाना\" -> मीडिया नियंत्रण",
                        "• \"लूना, चुप हो जाओ\" -> आवाज़ तुरंत शांत हो जाएगी"
                    )

                    commands.forEach { cmd ->
                        Text(
                            text = cmd,
                            style = MaterialTheme.typography.bodySmall,
                            color = LunaTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
