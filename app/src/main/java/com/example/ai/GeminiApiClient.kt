package com.example.ai

import android.content.Context
import com.example.BuildConfig
import com.example.ict.IctTradingKnowledge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class ParsedIntent(
    val actionType: String?, // WHATSAPP, YOUTUBE, MEDIA, ALARM, TIMER, WEB_SEARCH, CALENDAR, ICT, MEMORY
    val payload: Map<String, String>,
    val spokenResponseHi: String,
    val displayResponseHi: String
)

object GeminiApiClient {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
आप LUNA (लूना) हैं — एक व्यक्तिगत, बुद्धिमान, विनम्र और सहायक हिंदी वॉइस असिस्टेंट एवं ICT (Inner Circle Trader) ट्रेडिंग विशेषज्ञ।

महत्वपूर्ण निर्देश:
1. भाषा: आपकी प्राथमिक भाषा प्राकृतिक, धाराप्रवाह भारतीय हिंदी (Hindi) है। आप देवनागरी और Hinglish दोनों समझते हैं। उत्तर हमेशा शुद्ध और सहज हिंदी में दें।
2. टोन: सम्मानजनक, संक्षिप्त, आत्मीय और कार्य-उन्मुख। (जैसे: "जी हाँ", "बिल्कुल", "मैंने कर दिया है")।
3. एक्शन इंटेंट: यदि उपयोगकर्ता कोई कार्य (जैसे WhatsApp, YouTube, अलार्म, टाइमर, वेब सर्च, मीडिया कंट्रोल, ट्रेडिंग नोट या रिस्क कैलकुलेशन) करने को कहे, तो अपने उत्तर के अंत में उपयुक्त टैग अवश्य शामिल करें:
   - WhatsApp संदेश: [ACTION:WHATSAPP phone="फोन_या_खाली" msg="संदेश_टेक्स्ट"]
   - YouTube/म्यूजिक खोज: [ACTION:YOUTUBE query="खोज_वाक्यांश"]
   - मीडिया नियंत्रण: [ACTION:MEDIA cmd="play|pause|next|stop"]
   - अलार्म: [ACTION:ALARM hour="7" min="0" msg="लेबल"]
   - टाइमर: [ACTION:TIMER seconds="300" msg="लेबल"]
   - वेब सर्च: [ACTION:WEB_SEARCH query="सर्च_क्वेरी"]
   - मेमोरी सहेजें: [ACTION:MEMORY key="कीवर्ड" val="जानकारी"]
   - ICT ट्रेडिंग: यदि ICT कांसेप्ट पर चर्चा हो, तो स्पष्ट हिंदी में Market Structure, FVG, Liquidity Sweep, Order Block आदि समझाएं।
4. उपयोगकर्ता द्वारा "चुप हो जाओ" या "रुक जाओ" कहने पर विनम्रता से रुकने की पुष्टि करें।
"""

    /**
     * Process user prompt either via Gemini REST API or smart local fallback.
     */
    suspend fun processQuery(
        userPrompt: String,
        recentContext: List<Pair<String, String>> = emptyList()
    ): ParsedIntent = withContext(Dispatchers.IO) {
        val trimmed = userPrompt.trim()

        // 1. Check for immediate explicit local commands (Stop, Silence)
        if (isStopCommand(trimmed)) {
            return@withContext ParsedIntent(
                actionType = "MEDIA",
                payload = mapOf("cmd" to "stop"),
                spokenResponseHi = "ठीक है, मैं चुप हो रही हूँ।",
                displayResponseHi = "चुप हो गई हूँ। जब भी जरूरत हो, 'Hey Luna' बोलें।"
            )
        }

        // 2. Try Gemini API if key is available
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResponse = callGeminiApi(apiKey, trimmed, recentContext)
                if (apiResponse != null) {
                    return@withContext parseAssistantOutput(apiResponse)
                }
            } catch (e: Exception) {
                // Fallback to local heuristic engine
            }
        }

        // 3. Fallback: Intelligent Local Rule Engine (Works offline or before API key setup)
        return@withContext parseLocally(trimmed)
    }

    private fun isStopCommand(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("चुप हो जाओ") || lower.contains("chup ho jao") ||
                lower.contains("chup raho") || lower.contains("stop speaking") ||
                lower.contains("bolna band karo") || lower.contains("चुप रहो") ||
                lower == "चुप" || lower == "stop"
    }

    private fun callGeminiApi(
        apiKey: String,
        prompt: String,
        recentContext: List<Pair<String, String>>
    ): String? {
        val contentsArray = JSONArray()

        // Append recent conversational context
        for ((sender, message) in recentContext.takeLast(4)) {
            val role = if (sender == "USER") "user" else "model"
            contentsArray.put(JSONObject().apply {
                put("role", role)
                put("parts", JSONArray().put(JSONObject().put("text", message)))
            })
        }

        // Append current prompt
        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        })

        val requestBodyJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", SYSTEM_PROMPT)))
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 800)
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            return null
        }

        val responseBody = response.body?.string() ?: return null
        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val firstPart = parts.optJSONObject(0) ?: return null
        return firstPart.optString("text")
    }

    /**
     * Extracts tags like [ACTION:WHATSAPP msg="..."] from the model's text.
     */
    fun parseAssistantOutput(rawText: String): ParsedIntent {
        var cleanText = rawText
        var actionType: String? = null
        val payload = mutableMapOf<String, String>()

        val actionPattern = Pattern.compile("\\[ACTION:([A-Z_]+)(.*?)\\]")
        val matcher = actionPattern.matcher(rawText)

        if (matcher.find()) {
            actionType = matcher.group(1)
            val argsString = matcher.group(2) ?: ""

            // Parse key="value" pairs
            val kvPattern = Pattern.compile("(\\w+)=\"([^\"]*)\"")
            val kvMatcher = kvPattern.matcher(argsString)
            while (kvMatcher.find()) {
                val key = kvMatcher.group(1) ?: ""
                val value = kvMatcher.group(2) ?: ""
                if (key.isNotEmpty()) {
                    payload[key] = value
                }
            }

            cleanText = rawText.replace(matcher.group(0) ?: "", "").trim()
        }

        // Also check if text contains trading keywords for UI guidance
        return ParsedIntent(
            actionType = actionType,
            payload = payload,
            spokenResponseHi = cleanText,
            displayResponseHi = cleanText
        )
    }

    /**
     * Local Offline / Built-in Hindi Intent Parser
     */
    fun parseLocally(query: String): ParsedIntent {
        val lower = query.lowercase()

        // 1. WhatsApp Commands
        if (lower.contains("whatsapp") || lower.contains("व्हाट्सएप") || lower.contains("व्हाट्सऐप") || lower.contains("मैसेज")) {
            val msgMatch = Regex("(लिखो|भेजो|कह दो|send|message)\\s*(?:कि|:)?\\s*(.*)", RegexOption.IGNORE_CASE).find(query)
            val extractedMsg = msgMatch?.groupValues?.getOrNull(2) ?: query
            return ParsedIntent(
                actionType = "WHATSAPP",
                payload = mapOf("msg" to extractedMsg),
                spokenResponseHi = "मैंने WhatsApp में यह संदेश तैयार कर दिया है। कृपया जांच कर भेजें।",
                displayResponseHi = "WhatsApp संदेश तैयार किया गया: \"$extractedMsg\""
            )
        }

        // 2. YouTube / Music Commands
        if (lower.contains("youtube") || lower.contains("यूट्यूब") || lower.contains("गाना") ||
            lower.contains("video") || lower.contains("song") || lower.contains("चलाओ") || lower.contains("सर्च करो")) {

            val searchQuery = query
                .replace(Regex("(यूट्यूब पर|youtube par|चलाओ|खोजो|प्ले करो|गाना|video|song)", RegexOption.IGNORE_CASE), "")
                .trim()
                .ifEmpty { "Latest trending Hindi songs" }

            return ParsedIntent(
                actionType = "YOUTUBE",
                payload = mapOf("query" to searchQuery),
                spokenResponseHi = "YouTube पर \"$searchQuery\" खोल रही हूँ।",
                displayResponseHi = "YouTube पर \"$searchQuery\" खोजा जा रहा है।"
            )
        }

        // 3. Media Controls
        if (lower.contains("गाना रोको") || lower.contains("अगला गाना") || lower.contains("pause") || lower.contains("next")) {
            val cmd = if (lower.contains("अगला") || lower.contains("next")) "next" else "pause"
            return ParsedIntent(
                actionType = "MEDIA",
                payload = mapOf("cmd" to cmd),
                spokenResponseHi = "मीडिया कमांड निष्पादित किया गया।",
                displayResponseHi = "मीडिया प्लेयर: $cmd"
            )
        }

        // 4. Alarm / Timer
        if (lower.contains("अलार्म") || lower.contains("alarm")) {
            return ParsedIntent(
                actionType = "ALARM",
                payload = mapOf("hour" to "6", "min" to "0", "msg" to "Luna Morning Alarm"),
                spokenResponseHi = "अलार्म सेट करने की प्रक्रिया शुरू कर दी गई है।",
                displayResponseHi = "घड़ी में अलार्म सेट किया जा रहा है।"
            )
        }

        if (lower.contains("टाइमर") || lower.contains("timer")) {
            return ParsedIntent(
                actionType = "TIMER",
                payload = mapOf("seconds" to "300", "msg" to "Luna Timer"),
                spokenResponseHi = "5 मिनट का टाइमर सेट किया जा रहा है।",
                displayResponseHi = "5 मिनट (300 सेकंड) का टाइमर सेट किया गया।"
            )
        }

        // 5. ICT Trading queries
        if (lower.contains("ict") || lower.contains("ट्रेडिंग") || lower.contains("fvg") ||
            lower.contains("liquidity") || lower.contains("order block") || lower.contains("bos") ||
            lower.contains("choch") || lower.contains("स्वीप") || lower.contains("कैंडल")) {

            val matchingConcept = IctTradingKnowledge.concepts.firstOrNull { concept ->
                lower.contains(concept.id) ||
                        lower.contains(concept.nameEn.lowercase()) ||
                        lower.contains(concept.nameHi.lowercase())
            } ?: IctTradingKnowledge.concepts[0]

            val text = "${matchingConcept.nameHi}\n\n${matchingConcept.summaryHi}\n\n${matchingConcept.detailedExplanationHi}\n\nउदाहरण: ${matchingConcept.exampleScenarioHi}"

            return ParsedIntent(
                actionType = "ICT_CONCEPT",
                payload = mapOf("conceptId" to matchingConcept.id),
                spokenResponseHi = "${matchingConcept.nameHi} के बारे में: ${matchingConcept.summaryHi}",
                displayResponseHi = text
            )
        }

        // 6. Web Search
        if (lower.contains("सर्च") || lower.contains("search") || lower.contains("गूगल") || lower.contains("खबर")) {
            val queryClean = query.replace(Regex("(सर्च करो|ढूंढो|search|google)", RegexOption.IGNORE_CASE), "").trim()
            return ParsedIntent(
                actionType = "WEB_SEARCH",
                payload = mapOf("query" to queryClean),
                spokenResponseHi = "\"$queryClean\" के लिए वेब सर्च खोला जा रहा है।",
                displayResponseHi = "ब्राउज़र में खोज: $queryClean"
            )
        }

        // Default Assistant Answer
        val defaultText = "नमस्ते! मैं आपकी पर्सनल असिस्टेंट LUNA हूँ। मैं आपकी आवाज़ सुनकर WhatsApp मैसेज लिखने, YouTube पर वीडियो चलाने, अलार्म लगाने और ICT ट्रेडिंग सिखाने में पूरी मदद कर सकती हूँ।"
        return ParsedIntent(
            actionType = null,
            payload = emptyMap(),
            spokenResponseHi = defaultText,
            displayResponseHi = defaultText
        )
    }
}
