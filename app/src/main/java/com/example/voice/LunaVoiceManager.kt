package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class LunaVoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "LunaVoiceManager"

    // Text to Speech
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    // Speech Recognizer
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListeningInternal = false

    // State Flows for UI
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _liveTranscription = MutableStateFlow("")
    val liveTranscription: StateFlow<String> = _liveTranscription.asStateFlow()

    private val _audioLevelRms = MutableStateFlow(0f)
    val audioLevelRms: StateFlow<Float> = _audioLevelRms.asStateFlow()

    private val _isWakeWordMode = MutableStateFlow(true)
    val isWakeWordMode: StateFlow<Boolean> = _isWakeWordMode.asStateFlow()

    // Callbacks
    var onCommandRecognized: ((String) -> Unit)? = null
    var onWakeWordHeard: (() -> Unit)? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        initTts()
        initSpeechRecognizer()
    }

    private fun initTts() {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(tag, "TTS init error: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindiLocale = Locale("hi", "IN")
            val result = textToSpeech?.setLanguage(hindiLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default or English Indian
                textToSpeech?.setLanguage(Locale("hi"))
            }
            textToSpeech?.setPitch(1.05f) // Slight feminine clear tone for Luna
            textToSpeech?.setSpeechRate(0.95f) // Natural pacing for Hindi

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            isTtsReady = true
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            mainHandler.post {
                try {
                    speechRecognizer?.destroy()
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
                        setRecognitionListener(createRecognitionListener())
                    }
                } catch (e: Exception) {
                    Log.e(tag, "SpeechRecognizer create failed: ${e.message}")
                }
            }
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize 0f to 10f for the glowing orb animation
                val normalized = (rmsdB.coerceIn(0f, 10f) / 10f)
                _audioLevelRms.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _audioLevelRms.value = 0f
            }

            override fun onError(error: Int) {
                Log.d(tag, "Speech error code: $error")
                _isListening.value = false
                _audioLevelRms.value = 0f

                // If wake word continuous listening mode is on, restart listening gracefully
                if (isListeningInternal && _isWakeWordMode.value) {
                    mainHandler.postDelayed({
                        if (isListeningInternal) {
                            startListeningInternal()
                        }
                    }, 500)
                }
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                _audioLevelRms.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull() ?: ""

                if (recognizedText.isNotBlank()) {
                    _liveTranscription.value = recognizedText
                    handleRecognizedPhrase(recognizedText)
                }

                // If in continuous wake-word standby, resume
                if (isListeningInternal && _isWakeWordMode.value) {
                    mainHandler.postDelayed({
                        if (isListeningInternal) {
                            startListeningInternal()
                        }
                    }, 400)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    _liveTranscription.value = partial
                    // Check for immediate wake word or silence command
                    if (isStopPhrase(partial)) {
                        stopSpeaking()
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun isStopPhrase(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("चुप") || lower.contains("chup") || lower.contains("stop") || lower.contains("रुको")
    }

    private fun handleRecognizedPhrase(text: String) {
        val lower = text.lowercase()

        // Immediate silence
        if (isStopPhrase(lower)) {
            stopSpeaking()
            return
        }

        // Wake word trigger: "Hey Luna", "लूना", "हे लूना"
        val isWakeWordPresent = lower.contains("luna") ||
                lower.contains("लूना") ||
                lower.contains("हे लूना") ||
                lower.contains("hey luna")

        if (isWakeWordPresent) {
            triggerVibration()
            onWakeWordHeard?.invoke()

            // Strip the wake word and process command if any
            val command = text.replace(Regex("(hey luna|लूना|हे लूना|luna)", RegexOption.IGNORE_CASE), "").trim()
            if (command.isNotBlank()) {
                onCommandRecognized?.invoke(command)
            } else {
                speak("हाँ, मैं सुन रही हूँ। आज्ञा दीजिए।")
            }
        } else {
            // General speech command when active
            onCommandRecognized?.invoke(text)
        }
    }

    fun startListening() {
        // If speaking, stop speaking immediately so Luna doesn't hear herself
        stopSpeaking()
        isListeningInternal = true
        startListeningInternal()
    }

    private fun startListeningInternal() {
        mainHandler.post {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e(tag, "Start listening error: ${e.message}")
            }
        }
    }

    fun stopListening() {
        isListeningInternal = false
        _isListening.value = false
        _audioLevelRms.value = 0f
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(tag, "Stop listening error: ${e.message}")
            }
        }
    }

    fun toggleListening() {
        if (_isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!isTtsReady || text.isBlank()) return
        stopSpeaking()

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "luna_${System.currentTimeMillis()}")
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, params.getString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(tag, "Stop speaking error: ${e.message}")
        }
    }

    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
    }

    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch.coerceIn(0.5f, 2.0f))
    }

    private fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(60)
        } catch (e: Exception) {
            // Ignore vibration error
        }
    }

    fun setWakeWordContinuousMode(enabled: Boolean) {
        _isWakeWordMode.value = enabled
        if (!enabled && isListeningInternal) {
            stopListening()
        }
    }

    fun release() {
        stopListening()
        stopSpeaking()
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
