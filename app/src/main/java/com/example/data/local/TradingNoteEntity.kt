package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trading_notes")
data class TradingNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val concept: String, // e.g. "FVG", "Liquidity Sweep", "Order Block", "BOS"
    val pair: String, // e.g. "EUR/USD", "NIFTY50", "BANKNIFTY", "XAU/USD"
    val bias: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val notes: String,
    val riskReward: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
