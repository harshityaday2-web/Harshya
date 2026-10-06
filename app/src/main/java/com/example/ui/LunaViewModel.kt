package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.LunaApp
import com.example.actions.AppActionExecutor
import com.example.actions.ExecutionResult
import com.example.ai.GeminiApiClient
import com.example.ai.ParsedIntent
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MemoryItemEntity
import com.example.data.local.TradingNoteEntity
import com.example.ict.IctTradingKnowledge
import com.example.ict.RiskRewardCalcResult
import com.example.service.LunaVoiceForegroundService
import com.example.ui.components.AssistantOrbState
import com.example.voice.LunaVoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LunaTab(val titleHi: String, val iconName: String) {
    ASSISTANT("LUNA असिस्टेंट", "assistant"),
    ICT_TRADING("ICT ट्रेडिंग", "trending_up"),
    APP_ACTIONS("ऐप नियंत्रण", "apps"),
    MEMORY("मेरी मेमोरी", "psychology"),
    SETTINGS("सेटिंग्स और सहायता", "settings")
}

data class PendingActionConfirmation(
    val actionType: String,
    val descriptionHi: String,
    val payload: Map<String, String>,
    val onConfirm: () -> Unit
)

class LunaViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val dao = (application as LunaApp).database.lunaDao()

    val voiceManager = LunaVoiceManager(context)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(LunaTab.ASSISTANT)
    val currentTab: StateFlow<LunaTab> = _currentTab.asStateFlow()

    // Assistant State
    private val _orbState = MutableStateFlow(AssistantOrbState.IDLE)
    val orbState: StateFlow<AssistantOrbState> = _orbState.asStateFlow()

    private val _statusTextHi = MutableStateFlow("नमस्ते! मैं LUNA हूँ। आप क्या करना चाहते हैं?")
    val statusTextHi: StateFlow<String> = _statusTextHi.asStateFlow()

    // Messages from DB
    val chatMessages: StateFlow<List<ChatMessageEntity>> = dao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Memories from DB
    val memoryItems: StateFlow<List<MemoryItemEntity>> = dao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trading notes from DB
    val tradingNotes: StateFlow<List<TradingNoteEntity>> = dao.getAllTradingNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending confirmation for sensitive actions like WhatsApp message
    private val _pendingAction = MutableStateFlow<PendingActionConfirmation?>(null)
    val pendingAction: StateFlow<PendingActionConfirmation?> = _pendingAction.asStateFlow()

    // Foreground service toggle
    private val _isForegroundServiceActive = MutableStateFlow(false)
    val isForegroundServiceActive: StateFlow<Boolean> = _isForegroundServiceActive.asStateFlow()

    // Risk Reward Calculator State
    private val _rrResult = MutableStateFlow<RiskRewardCalcResult?>(null)
    val rrResult: StateFlow<RiskRewardCalcResult?> = _rrResult.asStateFlow()

    init {
        // Listen to voice manager callbacks
        voiceManager.onCommandRecognized = { command ->
            handleUserQuery(command)
        }

        voiceManager.onWakeWordHeard = {
            _statusTextHi.value = "हाँ! मैं सुन रही हूँ…"
            _orbState.value = AssistantOrbState.LISTENING
        }

        // Connect voiceManager state with orb
        viewModelScope.launch {
            combine(
                voiceManager.isListening,
                voiceManager.isSpeaking,
                _orbState
            ) { isList, isSpk, curState ->
                when {
                    isList -> AssistantOrbState.LISTENING
                    isSpk -> AssistantOrbState.SPEAKING
                    curState == AssistantOrbState.THINKING -> AssistantOrbState.THINKING
                    else -> AssistantOrbState.IDLE
                }
            }.collect { newState ->
                if (_orbState.value != AssistantOrbState.THINKING || newState == AssistantOrbState.SPEAKING) {
                    _orbState.value = newState
                }
            }
        }

        // Initialize default greeting and memories if empty
        viewModelScope.launch {
            if (chatMessages.value.isEmpty()) {
                dao.insertMessage(
                    ChatMessageEntity(
                        text = "नमस्ते! मैं आपकी व्यक्तिगत AI असिस्टेंट LUNA हूँ। मैं शुद्ध हिंदी और Hinglish समझ सकती हूँ।\n\nआप मुझसे WhatsApp संदेश भेजने, YouTube पर गाने या ICT ट्रेडिंग ट्यूटोरियल चलाने, अलार्म लगाने या मार्केट स्ट्रक्चर समझने के लिए कह सकते हैं।",
                        sender = "LUNA"
                    )
                )
            }
        }
    }

    fun setTab(tab: LunaTab) {
        _currentTab.value = tab
    }

    fun toggleVoiceListening() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
            _statusTextHi.value = "माइक बंद किया गया। 'Hey Luna' बोलें या माइक टैप करें।"
        } else {
            voiceManager.stopSpeaking()
            voiceManager.startListening()
            _statusTextHi.value = "LUNA सुन रही है… (बोलिए)"
        }
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _orbState.value = AssistantOrbState.IDLE
        _statusTextHi.value = "आवाज़ रोक दी गई है।"
    }

    fun sendTextQuery(query: String) {
        if (query.isNotBlank()) {
            handleUserQuery(query)
        }
    }

    private fun handleUserQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        voiceManager.stopSpeaking()

        viewModelScope.launch {
            // 1. Record User Message
            dao.insertMessage(
                ChatMessageEntity(
                    text = trimmed,
                    sender = "USER"
                )
            )

            _statusTextHi.value = "LUNA विचार कर रही है…"
            _orbState.value = AssistantOrbState.THINKING

            // 2. Fetch context for conversation
            val recentTurns = chatMessages.value.takeLast(6).map {
                it.sender to it.text
            }

            // 3. Process via Gemini AI / Local Engine
            val parsedIntent: ParsedIntent = GeminiApiClient.processQuery(trimmed, recentTurns)

            _orbState.value = AssistantOrbState.SPEAKING
            _statusTextHi.value = parsedIntent.spokenResponseHi

            // 4. Save Luna's Response to Database
            dao.insertMessage(
                ChatMessageEntity(
                    text = parsedIntent.displayResponseHi,
                    sender = "LUNA",
                    actionType = parsedIntent.actionType,
                    actionPayload = parsedIntent.payload.toString()
                )
            )

            // 5. Speak response out loud in Hindi
            voiceManager.speak(parsedIntent.spokenResponseHi)

            // 6. Handle Action Intent
            handleActionIntent(parsedIntent)
        }
    }

    private fun handleActionIntent(intent: ParsedIntent) {
        when (intent.actionType) {
            "WHATSAPP" -> {
                val phone = intent.payload["phone"]
                val msg = intent.payload["msg"] ?: "नमस्ते"
                // Ask confirmation or directly prepare
                _pendingAction.value = PendingActionConfirmation(
                    actionType = "WHATSAPP",
                    descriptionHi = "WhatsApp पर संदेश: \"$msg\"",
                    payload = intent.payload,
                    onConfirm = {
                        val result = AppActionExecutor.openWhatsApp(context, phone, msg)
                        _statusTextHi.value = result.messageInHindi
                        _pendingAction.value = null
                    }
                )
            }
            "YOUTUBE" -> {
                val query = intent.payload["query"] ?: "ICT Trading Tutorial Hindi"
                val result = AppActionExecutor.openYouTube(context, query)
                _statusTextHi.value = result.messageInHindi
            }
            "MEDIA" -> {
                val cmd = intent.payload["cmd"] ?: "pause"
                if (cmd == "stop") {
                    stopSpeaking()
                } else {
                    val result = AppActionExecutor.controlMedia(context, cmd)
                    _statusTextHi.value = result.messageInHindi
                }
            }
            "ALARM" -> {
                val hour = intent.payload["hour"]?.toIntOrNull() ?: 7
                val min = intent.payload["min"]?.toIntOrNull() ?: 0
                val msg = intent.payload["msg"] ?: "LUNA Alarm"
                val result = AppActionExecutor.setAlarm(context, hour, min, msg)
                _statusTextHi.value = result.messageInHindi
            }
            "TIMER" -> {
                val seconds = intent.payload["seconds"]?.toIntOrNull() ?: 300
                val msg = intent.payload["msg"] ?: "LUNA Timer"
                val result = AppActionExecutor.setTimer(context, seconds, msg)
                _statusTextHi.value = result.messageInHindi
            }
            "WEB_SEARCH" -> {
                val query = intent.payload["query"] ?: ""
                val result = AppActionExecutor.openWebSearch(context, query)
                _statusTextHi.value = result.messageInHindi
            }
            "MEMORY" -> {
                val key = intent.payload["key"] ?: "पसंद"
                val value = intent.payload["val"] ?: ""
                saveMemory(key, value, "PREFERENCE")
            }
        }
    }

    fun confirmPendingAction() {
        _pendingAction.value?.onConfirm?.invoke()
    }

    fun dismissPendingAction() {
        _pendingAction.value = null
        _statusTextHi.value = "कार्रवाई रद्द कर दी गई।"
    }

    // Memory operations
    fun saveMemory(key: String, value: String, category: String = "PREFERENCE") {
        viewModelScope.launch {
            dao.insertMemory(
                MemoryItemEntity(
                    key = key.trim(),
                    value = value.trim(),
                    category = category
                )
            )
            _statusTextHi.value = "मेमोरी सहेज ली गई है।"
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            dao.deleteMemoryById(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            dao.clearAllMemories()
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            dao.clearAllMessages()
        }
    }

    // Trading Note Operations
    fun saveTradingNote(
        title: String,
        concept: String,
        pair: String,
        bias: String,
        notes: String,
        riskReward: String?
    ) {
        viewModelScope.launch {
            dao.insertTradingNote(
                TradingNoteEntity(
                    title = title.ifBlank { "ट्रेडिंग नोट" },
                    concept = concept,
                    pair = pair.ifBlank { "NIFTY/BANKNIFTY" },
                    bias = bias,
                    notes = notes,
                    riskReward = riskReward
                )
            )
            _statusTextHi.value = "ICT ट्रेडिंग नोट सहेज लिया गया है।"
        }
    }

    fun deleteTradingNote(id: Long) {
        viewModelScope.launch {
            dao.deleteTradingNoteById(id)
        }
    }

    fun calculateRiskReward(
        capital: Double,
        riskPercent: Double,
        entryPrice: Double,
        stopLoss: Double,
        takeProfit: Double
    ) {
        val result = IctTradingKnowledge.calculateRiskReward(
            capital, riskPercent, entryPrice, stopLoss, takeProfit
        )
        _rrResult.value = result
    }

    // Foreground service toggle
    fun toggleForegroundService() {
        val newState = !_isForegroundServiceActive.value
        val intent = Intent(context, LunaVoiceForegroundService::class.java).apply {
            action = if (newState) LunaVoiceForegroundService.ACTION_START else LunaVoiceForegroundService.ACTION_STOP
        }

        try {
            if (newState) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                _isForegroundServiceActive.value = true
                voiceManager.setWakeWordContinuousMode(true)
            } else {
                context.stopService(intent)
                _isForegroundServiceActive.value = false
                voiceManager.setWakeWordContinuousMode(false)
            }
        } catch (e: Exception) {
            _statusTextHi.value = "सर्विस त्रुटि: ${e.message}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
