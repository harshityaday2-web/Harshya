package com.example

import com.example.ai.GeminiApiClient
import com.example.ict.IctTradingKnowledge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testRiskRewardCalculation() {
        val result = IctTradingKnowledge.calculateRiskReward(
            capital = 100000.0,
            riskPercent = 1.0,
            entryPrice = 100.0,
            stopLoss = 95.0,
            takeProfit = 110.0
        )
        // Risk = 5 per unit, Reward = 10 per unit, R:R = 2.0
        assertEquals(2.0, result.riskRewardRatio, 0.01)
        assertEquals(1000.0, result.riskAmount, 0.01)
        assertEquals(2000.0, result.rewardAmount, 0.01)
        assertEquals(200.0, result.positionSizeUnits, 0.01)
        assertTrue(result.statusTextHi.contains("उत्कृष्ट ICT"))
    }

    @Test
    fun testActionTagParsing() {
        val output = "मैं गाना चला रही हूँ। [ACTION:YOUTUBE query=\"Arijit Singh\"]"
        val parsed = GeminiApiClient.parseAssistantOutput(output)
        assertEquals("YOUTUBE", parsed.actionType)
        assertEquals("Arijit Singh", parsed.payload["query"])
        assertEquals("मैं गाना चला रही हूँ।", parsed.spokenResponseHi)
    }

    @Test
    fun testLocalFallbackWhatsApp() {
        val query = "राहुल को व्हाट्सएप पर भेजो कि मैं पाँच मिनट में आ रहा हूँ"
        val parsed = GeminiApiClient.parseLocally(query)
        assertEquals("WHATSAPP", parsed.actionType)
        assertNotNull(parsed.payload["msg"])
    }
}
