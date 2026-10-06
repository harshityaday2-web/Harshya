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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun MemoryScreen(
    viewModel: LunaViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memoryItems.collectAsState()
    val tradingNotes by viewModel.tradingNotes.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("PREFERENCE") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = "Memory", tint = LunaPurpleSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "पर्सनल मेमोरी और ट्रेडिंग जर्नल",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LunaPurpleSecondary
                    )
                }
                Text(
                    text = "LUNA आपके द्वारा दी गई अनुमत प्राथमिकताओं और ट्रेडिंग नोट्स को सुरक्षित रखती है।",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary
                )
            }
        }

        // Privacy Guarantee Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "सुरक्षा",
                        tint = LunaBullishGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "सुरक्षा नियम: पासवर्ड, बैंक पिन या प्रमाणीकरण टोकन कभी भी मेमोरी में सहेजे नहीं जाते। पूरा डेटा आपके डिवाइस के लोकल रूम डेटाबेस में सुरक्षित है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Add Memory Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "नयी प्राथमिकता या मेमोरी जोड़ें",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LunaCyanPrimary
                    )

                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        label = { Text("विषय / शीर्षक (उदा. पसंदीदा भाषा, पसंदीदा गायक)") },
                        modifier = Modifier.fillMaxWidth().testTag("memory_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        label = { Text("विवरण (उदा. हिंदी, अरिजीत सिंह, NIFTY 15m)") },
                        modifier = Modifier.fillMaxWidth().testTag("memory_val_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            if (newKey.isNotBlank() && newValue.isNotBlank()) {
                                viewModel.saveMemory(newKey, newValue, newCategory)
                                newKey = ""
                                newValue = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("save_memory_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("मेमोरी में सुरक्षित करें", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Memories List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "सहेजी गई प्राथमिकताएँ (${memories.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LunaTextPrimary
                )

                if (memories.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { viewModel.clearAllMemories() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LunaBearishRed)
                    ) {
                        Text("सभी हटाएं", fontSize = 11.sp)
                    }
                }
            }
        }

        if (memories.isEmpty()) {
            item {
                Text(
                    text = "अभी तक कोई व्यक्तिगत प्राथमिकता सहेजी नहीं गई है। LUNA से बातचीत के दौरान भी 'यह मेरी पसंद है' कहकर सहेज सकते हैं।",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary
                )
            }
        } else {
            items(memories, key = { it.id }) { memory ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = memory.key,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LunaCyanPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = memory.value,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LunaTextPrimary
                            )
                        }

                        IconButton(onClick = { viewModel.deleteMemory(memory.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = LunaBearishRed)
                        }
                    }
                }
            }
        }

        // Trading Journal Notes Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Journal", tint = LunaAmberTertiary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ICT ट्रेडिंग जर्नल नोट्स (${tradingNotes.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = LunaAmberTertiary
                    )
                }
            }
        }

        if (tradingNotes.isEmpty()) {
            item {
                Text(
                    text = "कोई ट्रेडिंग नोट उपलब्ध नहीं है। आप ICT ट्रेडिंग स्क्रीन से रिस्क कैलकुलेशन या सेटअप जर्नल में सेव कर सकते हैं।",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary
                )
            }
        } else {
            items(tradingNotes, key = { it.id }) { note ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (note.bias == "BULLISH") LunaBullishGreen else LunaBearishRed
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${note.title} • ${note.pair}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LunaTextPrimary
                            )

                            IconButton(onClick = { viewModel.deleteTradingNote(note.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = LunaBearishRed)
                            }
                        }

                        Surface(
                            color = LunaDarkSurfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "कांसेप्ट: ${note.concept} | बायस: ${note.bias}${if (!note.riskReward.isNullOrBlank()) " | R:R: ${note.riskReward}" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LunaAmberTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = note.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = LunaTextSecondary
                        )
                    }
                }
            }
        }

        // Chat History clear button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.clearChatHistory() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LunaBearishRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Clear Chat", tint = LunaBearishRed)
                Spacer(modifier = Modifier.width(6.dp))
                Text("चैट हिस्ट्री साफ़ करें (Clear Chat History)")
            }
        }
    }
}
