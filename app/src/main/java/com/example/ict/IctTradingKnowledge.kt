package com.example.ict

data class IctConcept(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val summaryHi: String,
    val detailedExplanationHi: String,
    val exampleScenarioHi: String,
    val keyRulesHi: List<String>
)

data class KillZoneSession(
    val name: String,
    val nameHi: String,
    val utcTime: String,
    val istTime: String,
    val descriptionHi: String,
    val characteristicsHi: String
) {
    val hindiName: String get() = nameHi
}

data class RiskRewardCalcResult(
    val riskAmount: Double,
    val rewardAmount: Double,
    val riskRewardRatio: Double,
    val positionSizeUnits: Double,
    val riskPercentage: Double,
    val statusTextHi: String
)

object IctTradingKnowledge {

    val concepts = listOf(
        IctConcept(
            id = "market_structure",
            nameEn = "Market Structure",
            nameHi = "मार्केट स्ट्रक्चर (बाजार की संरचना)",
            summaryHi = "हायर हाई (HH), हायर लो (HL) या लोअर हाई (LH), लोअर लो (LL) द्वारा बाजार की दिशा तय करना।",
            detailedExplanationHi = "ICT में मार्केट स्ट्रक्चर बाजार की मूल दिशा (Order Flow) को दर्शाता है। अगर कीमत लगातार पिछले हाई को तोड़कर नए हाई बना रही है तो यह Bullish Structure है। जब यह पिछले मुख्य स्विंग लो को तोड़ती है तो स्ट्रक्चर में बदलाव (Market Structure Shift) का संकेत मिलता है।",
            exampleScenarioHi = "EUR/USD में 15-मिनट चार्ट पर यदि 1.0850 का स्विंग हाई मजबूत मोमेंटम के साथ टूटता है, तो बुल्लिश स्ट्रक्चर की पुष्टि होती है।",
            keyRulesHi = listOf(
                "हमेशा हायर टाइमफ्रेम (HTF - Daily/4H) का ट्रेंड देखें।",
                "केवल विक (Wick) नहीं, बल्कि कैंडल की बॉडी क्लोजिंग पर भरोसा करें।"
            )
        ),
        IctConcept(
            id = "bos_vs_choch",
            nameEn = "BOS vs CHOCH",
            nameHi = "Break of Structure (BOS) बनाम Change of Character (CHOCH)",
            summaryHi = "BOS ट्रेंड के जारी रहने का प्रमाण है, जबकि CHOCH ट्रेंड के उलटने का पहला संकेत है।",
            detailedExplanationHi = "BOS (Break of Structure): मौजूदा ट्रेंड की दिशा में ही किसी स्विंग हाई या लो का टूटना। यह ट्रेंड जारी रहने (Continuation) को प्रमाणित करता है।\nCHOCH / MSS: एक अपट्रेंड में पहला हायर लो टूटना या डाउनट्रेंड में पहला लोअर हाई टूटना। यह बताता है कि स्मार्ट मनी (Institutional Traders) अब दिशा बदल रहे हैं।",
            exampleScenarioHi = "अपट्रेंड में 1.0900 टूटना = BOS। इसके बाद यदि 1.0820 का महत्वपूर्ण स्विंग लो टूट जाता है = CHOCH (अब बेरिश रिवर्सल संभव)।",
            keyRulesHi = listOf(
                "BOS में ट्रेंड की दिशा में रीटेस्ट पर ट्रेड लें।",
                "CHOCH के तुरंत बाद आक्रामक ट्रेड न लें; FVG या Order Block बनने की प्रतीक्षा करें।"
            )
        ),
        IctConcept(
            id = "liquidity_sweep",
            nameEn = "Liquidity Sweep & Pools",
            nameHi = "लिक्विडिटी स्वीप (BSL एवं SSL)",
            summaryHi = "स्मार्ट मनी द्वारा रिटेल स्टॉप लॉस (Buy/Sell Stops) को ट्रिगर करके उल्टी दिशा में जाना।",
            detailedExplanationHi = "BSL (Buy-Side Liquidity): महत्वपूर्ण स्विंग हाई, इक्वल हाई (EQH) या PDH के ऊपर रिटेल सेलर्स के स्टॉप लॉस।\nSSL (Sell-Side Liquidity): स्विंग लो, इक्वल लो (EQL) या PDL के नीचे खरीदारों के स्टॉप लॉस।\nलिक्विडिटी स्वीप तब होता है जब कीमत इन लेवल्स को पार करके तुरंत रिवर्स हो जाती है (Wick बनाकर वापस आ जाती है)।",
            exampleScenarioHi = "मार्केट Asian High को लंदन ओपन के समय स्वीप करता है और फिर जोरदार गिरावट (Displacement) दिखाता है। इसे Judas Swing कहते हैं।",
            keyRulesHi = listOf(
                "मार्केट कभी भी बिना लिक्विडिटी के नहीं चलता; यह हमेशा अगले लिक्विडिटी पूल की ओर बढ़ता है।",
                "स्वीप के बाद Displacement कैंडल देखना अनिवार्य है।"
            )
        ),
        IctConcept(
            id = "fvg",
            nameEn = "Fair Value Gap (FVG)",
            nameHi = "फेयर वैल्यू गैप (इम्बैलेंस / असंतुलन)",
            summaryHi = "3 कैंडल का ऐसा पैटर्न जहाँ पहली कैंडल की विक और तीसरी कैंडल की विक के बीच खाली स्थान रह जाता है।",
            detailedExplanationHi = "जब स्मार्ट मनी एक तरफा भारी ऑर्डर्स डालती है, तो कीमत इतनी तेजी से भागती है कि दोनों तरफ पर्याप्त ट्रेडिंग नहीं हो पाती। इसे Imbalance (SIBI / BISI) कहते हैं। मार्केट बाद में इस असंतुलन को भरने (Rebalance करने) वापस आता है। यही रिटेल ट्रेडर के लिए सबसे सुरक्षित री-एंट्री पॉइंट होता है।",
            exampleScenarioHi = "कैंडल 1 का हाई 100, कैंडल 2 बहुत बड़ी ग्रीन, कैंडल 3 का लो 105। 100 से 105 का 5 पॉइंट का खाली गैप = Bullish FVG।",
            keyRulesHi = listOf(
                "FVG का 50% लेवल (Consequent Encroachment / CE) सबसे महत्वपूर्ण सपोर्ट/रेजिस्टेंस होता है।",
                "यदि FVG पूरी तरह भर जाए और उल्टी दिशा में ब्रेक हो जाए तो इसे Inversion FVG कहते हैं।"
            )
        ),
        IctConcept(
            id = "order_block",
            nameEn = "Order Block (OB) & Breakers",
            nameHi = "ऑर्डर ब्लॉक एवं ब्रेकर ब्लॉक",
            summaryHi = "तेज मूवमेंट (Displacement) से पहले की आखिरी विपरीत कैंडल जहाँ बैंकों के पेंडिंग ऑर्डर्स होते हैं।",
            detailedExplanationHi = "Bullish Order Block: एक मजबूत ऊपर की रैली से पहले की अंतिम डाउन कैंडल (Bearish Candle)। जब कीमत वापस इस स्तर पर आती है, तो बाकी ऑर्डर्स एग्जीक्यूट होते हैं।\nBreaker Block: ऐसा ऑर्डर ब्लॉक जो लिक्विडिटी स्वीप करने के बाद विफल हो जाता है और फिर उलटी दिशा में सपोर्ट या रेजिस्टेंस बन जाता है।",
            exampleScenarioHi = "सपोर्ट लेवल के नीचे स्वीप करने के बाद बनी अंतिम लाल कैंडल, जिसके बाद 3 बड़ी हरी कैंडल बनीं। वह लाल कैंडल Bullish Order Block है।",
            keyRulesHi = listOf(
                "हमेशा वह ऑर्डर ब्लॉक चुनें जिसने लिक्विडिटी स्वीप की हो और FVG बनाया हो।",
                "कमजोर ऑर्डर ब्लॉक बिना FVG के आसानी से टूट जाते हैं।"
            )
        ),
        IctConcept(
            id = "premium_discount",
            nameEn = "Premium vs Discount Zones",
            nameHi = "प्रीमियम बनाम डिस्काउंट जोन (Dealing Range)",
            summaryHi = "फाइबोनाची 50% इक्विलिब्रियम के ऊपर प्रीमियम (बेचने की जगह), नीचे डिस्काउंट (खरीदने की जगह)।",
            detailedExplanationHi = "एक स्विंग लो से स्विंग हाई तक की रेंज को Dealing Range कहते हैं:\n- 50% से ऊपर (0.5 - 1.0) = Premium Zone (संस्थागत विक्रेता यहाँ बेचते हैं)।\n- 50% स्तर = Equilibrium (संतुलन)।\n- 50% से नीचे (0.0 - 0.5) = Discount Zone (संस्थागत खरीदार यहाँ खरीदते हैं)।\nOTE (Optimal Trade Entry): 0.618 से 0.786 का गोल्डन रेशियो।",
            exampleScenarioHi = "अपट्रेंड में कभी भी प्रीमियम (50% से ऊपर) में बाय न करें; कीमत के 62% या 70.5% डिस्काउंट पर आने का इंतजार करें।",
            keyRulesHi = listOf(
                "बाय हमेशा Discount Zone में FVG या Order Block पर करें।",
                "सेल हमेशा Premium Zone में करें।"
            )
        ),
        IctConcept(
            id = "kill_zones",
            nameEn = "Kill Zones & Sessions",
            nameHi = "किल जोन्स एवं ट्रेडिंग सेशंस",
            summaryHi = "दिन के वे खास घंटे जब दुनिया के सबसे बड़े बैंक और वित्तीय संस्थान सबसे ज्यादा वॉल्यूम लाते हैं।",
            detailedExplanationHi = "ICT के अनुसार पूरे दिन ट्रेड नहीं करना चाहिए। केवल मुख्य 3 किल जोन्स में उच्चतम संभावना वाले सेटअप्स मिलते हैं:\n1. Asian Session: रेंज और लिक्विडिटी बनाता है।\n2. London Killzone: अक्सर दिन का हाई या लो बनाता है (Judas Swing)।\n3. New York Killzone: ट्रेंड का विस्तार या रिवर्सल करता है।",
            exampleScenarioHi = "दोपहर 1:30 बजे (IST) लंदन ओपन पर एशियन हाई स्वीप होने के बाद 5 मिनट पर FVG बनने पर सेल एंट्री।",
            keyRulesHi = listOf(
                "सिल्वर बुलेट (Silver Bullet) न्यूयॉर्क सुबह 10:00 से 11:00 AM EST (7:30 - 8:30 PM IST) में 1 FVG आधारित मॉडल है।",
                "हाई इम्पैक्ट न्यूज (CPI, NFP, FOMC) के समय पहले 5-10 मिनट में ट्रेड से बचें।"
            )
        )
    )

    val killZones = listOf(
        KillZoneSession(
            name = "Asian Session",
            nameHi = "एशियन सेशन",
            utcTime = "00:00 - 06:00 UTC",
            istTime = "05:30 AM - 11:30 AM IST",
            descriptionHi = "मार्केट सामान्यतः एक छोटी कंसोलिडेशन रेंज बनाता है।",
            characteristicsHi = "एशियन सेशन का हाई (Asian High) और लो (Asian Low) लंदन या न्यूयॉर्क सेशन के लिए मुख्य लिक्विडिटी टारगेट बन जाता है।"
        ),
        KillZoneSession(
            name = "London Killzone",
            nameHi = "लंदन किल जोन",
            utcTime = "07:00 - 10:00 UTC",
            istTime = "12:30 PM - 03:30 PM IST",
            descriptionHi = "यूरोपीय बैंक्स खुलते हैं और उच्चतम वोलैटिलिटी आती है।",
            characteristicsHi = "अक्सर 'Judas Swing' (नकली ब्रेकआउट) बनाकर एशियन लिक्विडिटी स्वीप करता है और दिन का वास्तविक हाई या लो स्थापित करता है।"
        ),
        KillZoneSession(
            name = "New York AM Killzone",
            nameHi = "न्यूयॉर्क सुबह किल जोन",
            utcTime = "12:00 - 15:00 UTC",
            istTime = "05:30 PM - 08:30 PM IST",
            descriptionHi = "लंदन और न्यूयॉर्क का ओवरलैप; वॉल्यूम का चरम स्तर।",
            characteristicsHi = "सिल्वर बुलेट (Silver Bullet) 10:00 - 11:00 AM EST (7:30 - 8:30 PM IST)। लंदन ट्रेंड का कंटिन्युएशन या रिवर्सल।"
        ),
        KillZoneSession(
            name = "London Close Killzone",
            nameHi = "लंदन क्लोज किल जोन",
            utcTime = "15:00 - 17:00 UTC",
            istTime = "08:30 PM - 10:30 PM IST",
            descriptionHi = "यूरोपीय ट्रेडर्स अपनी पोजीशन बुक करते हैं।",
            characteristicsHi = "दिन के ट्रेंड का रीबॉउन्ड या कंसोलिडेशन।"
        )
    )

    /**
     * Calculates Risk:Reward ratio and recommended position sizing.
     */
    fun calculateRiskReward(
        capital: Double,
        riskPercent: Double,
        entryPrice: Double,
        stopLoss: Double,
        takeProfit: Double
    ): RiskRewardCalcResult {
        if (entryPrice <= 0 || stopLoss <= 0 || takeProfit <= 0 || capital <= 0) {
            return RiskRewardCalcResult(0.0, 0.0, 0.0, 0.0, 0.0, "अमान्य इनपुट")
        }

        val isLong = takeProfit > entryPrice
        val riskPerUnit = if (isLong) (entryPrice - stopLoss) else (stopLoss - entryPrice)
        val rewardPerUnit = if (isLong) (takeProfit - entryPrice) else (entryPrice - takeProfit)

        if (riskPerUnit <= 0 || rewardPerUnit <= 0) {
            return RiskRewardCalcResult(
                0.0, 0.0, 0.0, 0.0, riskPercent,
                "स्टॉप लॉस या टारगेट की दिशा गलत है। (लॉन्ग में SL < Entry, शॉर्ट में SL > Entry)"
            )
        }

        val totalRiskAmount = (capital * riskPercent) / 100.0
        val positionUnits = totalRiskAmount / riskPerUnit
        val totalRewardAmount = positionUnits * rewardPerUnit
        val ratio = rewardPerUnit / riskPerUnit

        val statusText = if (ratio >= 2.0) {
            "उत्कृष्ट ICT रिस्क-रिवॉर्ड (1:${String.format("%.2f", ratio)})। ट्रेड मान्य है।"
        } else if (ratio >= 1.5) {
            "मध्यम रिस्क-रिवॉर्ड (1:${String.format("%.2f", ratio)})। सख्त स्टॉप-लॉस रखें।"
        } else {
            "कमजोर रिस्क-रिवॉर्ड (1:${String.format("%.2f", ratio)})। ICT नियमों के अनुसार 1:2 से कम अनुपात में ट्रेड करने से बचें।"
        }

        return RiskRewardCalcResult(
            riskAmount = totalRiskAmount,
            rewardAmount = totalRewardAmount,
            riskRewardRatio = ratio,
            positionSizeUnits = positionUnits,
            riskPercentage = riskPercent,
            statusTextHi = statusText
        )
    }
}
