package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ict.IctConcept
import com.example.ict.IctTradingKnowledge
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
fun IctTradingScreen(
    viewModel: LunaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("अवधारणाएँ (Concepts)", "रिस्क कैलकुलेटर", "किल जोन्स (Killzones)", "ट्रेड चेकलिस्ट")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Section Header
        Surface(
            color = LunaDarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "ICT",
                        tint = LunaCyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ICT ट्रेडिंग विशेषज्ञ मॉड्यूल",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LunaCyanPrimary
                    )
                }
                Text(
                    text = "Inner Circle Trader सिद्धांत — हिंदी में संपूर्ण विश्लेषण",
                    style = MaterialTheme.typography.bodySmall,
                    color = LunaTextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = Color.Transparent,
                    contentColor = LunaCyanPrimary,
                    edgePadding = 0.dp
                ) {
                    sections.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedSection == index,
                            onClick = { selectedSection = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Section Content
        when (selectedSection) {
            0 -> IctConceptsList(viewModel)
            1 -> IctRiskCalculator(viewModel)
            2 -> IctKillzonesList()
            3 -> IctTradeChecklist()
        }
    }
}

@Composable
fun IctConceptsList(viewModel: LunaViewModel) {
    var expandedConceptId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                        imageVector = Icons.Default.Info,
                        contentDescription = "सूचना",
                        tint = LunaAmberTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "किसी भी कांसेप्ट को विस्तार से समझने के लिए उस पर टैप करें या LUNA से बोलकर पूछें (जैसे: 'FVG समझाओ')।",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextSecondary
                    )
                }
            }
        }

        items(IctTradingKnowledge.concepts, key = { it.id }) { concept ->
            val isExpanded = expandedConceptId == concept.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedConceptId = if (isExpanded) null else concept.id
                    },
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = if (isExpanded) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LunaCyanPrimary)) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = concept.nameHi,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LunaTextPrimary
                            )
                            Text(
                                text = concept.nameEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = LunaCyanPrimary
                            )
                        }

                        IconButton(onClick = {
                            expandedConceptId = if (isExpanded) null else concept.id
                        }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "विस्तार",
                                tint = LunaTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = concept.summaryHi,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LunaTextSecondary
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                text = "गहन विश्लेषण:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = LunaPurpleSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = concept.detailedExplanationHi,
                                style = MaterialTheme.typography.bodySmall,
                                color = LunaTextPrimary,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "उदाहरण सिनेरियो:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = LunaAmberTertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = concept.exampleScenarioHi,
                                style = MaterialTheme.typography.bodySmall,
                                color = LunaTextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "मुख्य नियम (Golden Rules):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = LunaBullishGreen
                            )
                            concept.keyRulesHi.forEach { rule ->
                                Text(
                                    text = "• $rule",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LunaTextSecondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.voiceManager.speak("${concept.nameHi}। ${concept.summaryHi}। ${concept.detailedExplanationHi}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "सुनें",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LUNA की आवाज़ में सुनें", color = Color.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IctRiskCalculator(viewModel: LunaViewModel) {
    var capital by remember { mutableStateOf("100000") }
    var riskPercent by remember { mutableStateOf("1.0") }
    var entryPrice by remember { mutableStateOf("100.0") }
    var stopLoss by remember { mutableStateOf("95.0") }
    var takeProfit by remember { mutableStateOf("115.0") }

    val rrResult by viewModel.rrResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ICT रिस्क-रिवॉर्ड एवं पोजीशन साइज कैलकुलेटर",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = LunaCyanPrimary
            )
            Text(
                text = "अनुशासित रिस्क मैनेजमेंट ICT ट्रेडिंग की रीढ़ है (न्यूनतम 1:2 R:R की सिफारिश)।",
                style = MaterialTheme.typography.bodySmall,
                color = LunaTextSecondary
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = capital,
                        onValueChange = { capital = it },
                        label = { Text("कुल पूँजी (Total Capital ₹/$)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calc_capital_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LunaTextPrimary,
                            unfocusedTextColor = LunaTextPrimary
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = riskPercent,
                            onValueChange = { riskPercent = it },
                            label = { Text("रिस्क % (1-2%)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_risk_percent_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = entryPrice,
                            onValueChange = { entryPrice = it },
                            label = { Text("एंट्री प्राइस") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_entry_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = stopLoss,
                            onValueChange = { stopLoss = it },
                            label = { Text("स्टॉप लॉस (SL)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_sl_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = takeProfit,
                            onValueChange = { takeProfit = it },
                            label = { Text("टारगेट (TP)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calc_tp_field"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LunaTextPrimary,
                                unfocusedTextColor = LunaTextPrimary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.calculateRiskReward(
                                capital = capital.toDoubleOrNull() ?: 100000.0,
                                riskPercent = riskPercent.toDoubleOrNull() ?: 1.0,
                                entryPrice = entryPrice.toDoubleOrNull() ?: 100.0,
                                stopLoss = stopLoss.toDoubleOrNull() ?: 95.0,
                                takeProfit = takeProfit.toDoubleOrNull() ?: 115.0
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calc_submit_button")
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculate", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("गणना करें (Calculate R:R)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        rrResult?.let { result ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (result.riskRewardRatio >= 2.0) LunaBullishGreen else LunaBearishRed
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "गणना का परिणाम (Calculation Result)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaCyanPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("रिस्क अनुपात (R:R):", color = LunaTextSecondary)
                            Text(
                                "1 : ${String.format("%.2f", result.riskRewardRatio)}",
                                fontWeight = FontWeight.Bold,
                                color = if (result.riskRewardRatio >= 2.0) LunaBullishGreen else LunaAmberTertiary
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("अधिकतम रिस्क राशि:", color = LunaTextSecondary)
                            Text("₹${String.format("%.2f", result.riskAmount)}", color = LunaBearishRed, fontWeight = FontWeight.SemiBold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("संभावित लाभ (Reward):", color = LunaTextSecondary)
                            Text("₹${String.format("%.2f", result.rewardAmount)}", color = LunaBullishGreen, fontWeight = FontWeight.SemiBold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("सुझाई गई क्वांटिटी (Position Units):", color = LunaTextSecondary)
                            Text("${String.format("%.1f", result.positionSizeUnits)} यूनिट", color = LunaTextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = result.statusTextHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (result.riskRewardRatio >= 2.0) LunaBullishGreen else LunaAmberTertiary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.voiceManager.speak("रिस्क रिवॉर्ड 1 अनुपात ${String.format("%.1f", result.riskRewardRatio)} है। ${result.statusTextHi}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LunaCyanPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("आवाज़ में सुनें", color = Color.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.saveTradingNote(
                                        title = "R:R सेटअप (${String.format("%.2f", result.riskRewardRatio)})",
                                        concept = "Risk Calculator",
                                        pair = "NIFTY/FOREX",
                                        bias = if (takeProfit.toDouble() > entryPrice.toDouble()) "BULLISH" else "BEARISH",
                                        notes = "Entry: $entryPrice, SL: $stopLoss, TP: $takeProfit. Units: ${result.positionSizeUnits}",
                                        riskReward = "1:${String.format("%.2f", result.riskRewardRatio)}"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LunaPurpleSecondary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("जर्नल में सेव करें", color = Color.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IctKillzonesList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "घड़ी", tint = LunaCyanPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ICT के अनुसार समय ही कीमत है (Time & Price Theory)। केवल किल जोन्स में सबसे साफ लिक्विडिटी मूव्स मिलते हैं।",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextSecondary
                    )
                }
            }
        }

        items(IctTradingKnowledge.killZones) { session ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.hindiName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = LunaCyanPrimary
                        )
                        Surface(
                            color = LunaDarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = session.istTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = LunaAmberTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = session.name, style = MaterialTheme.typography.labelSmall, color = LunaTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = session.descriptionHi, style = MaterialTheme.typography.bodySmall, color = LunaTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "विशेषता: ${session.characteristicsHi}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaPurpleSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun IctTradeChecklist() {
    val checklistItems = remember {
        listOf(
            "1. Higher Timeframe (Daily/4H) की दिशा और बायो तय किया?",
            "2. मुख्य लिक्विडिटी पूल (PDH, PDL, Asian Range, Equal Highs/Lows) स्वीप हुआ?",
            "3. Market Structure Shift (MSS) मजबूत Displacement के साथ हुआ?",
            "4. 3-कैंडल Fair Value Gap (FVG) या Order Block बना?",
            "5. एंट्री Discount Zone (Buy के लिए) या Premium Zone (Sell के लिए) में है?",
            "6. रिस्क रिवॉर्ड अनुपात न्यूनतम 1:2 या उससे अधिक है?",
            "7. हाई इम्पैक्ट न्यूज (FOMC/CPI/NFP) का समय चेक किया?"
        )
    }

    val checkedStates = remember { mutableStateMapOf<Int, Boolean>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "ICT हाई-प्रोबेबिलिटी ट्रेड चेकलिस्ट",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = LunaCyanPrimary
            )
            Text(
                text = "किसी भी वास्तविक ट्रेड में प्रवेश करने से पहले इन सभी 7 नियमों की पुष्टि करें।",
                style = MaterialTheme.typography.bodySmall,
                color = LunaTextSecondary
            )
        }

        items(checklistItems.indices.toList()) { index ->
            val isChecked = checkedStates[index] ?: false
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { checkedStates[index] = !isChecked },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) LunaDarkSurfaceVariant else LunaDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Check",
                        tint = if (isChecked) LunaBullishGreen else LunaTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = checklistItems[index],
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isChecked) LunaTextPrimary else LunaTextSecondary,
                        fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }

        item {
            val totalChecked = checkedStates.values.count { it }
            Card(
                colors = CardDefaults.cardColors(containerColor = LunaDarkSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "सेटअप स्कोर: $totalChecked / ${checklistItems.size}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (totalChecked >= 5) LunaBullishGreen else LunaAmberTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (totalChecked == 7) {
                            "पूर्ण ICT A+ सेटअप! रिस्क-मैनेजमेंट के साथ ट्रेड निष्पादित करें।"
                        } else if (totalChecked >= 5) {
                            "मध्यम संभावना वाला सेटअप। सतर्क रहें और छोटी क्वांटिटी रखें।"
                        } else {
                            "नियम अधूरे हैं। ICT के अनुसार ट्रेड करने से बचें।"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = LunaTextPrimary
                    )
                }
            }
        }
    }
}
