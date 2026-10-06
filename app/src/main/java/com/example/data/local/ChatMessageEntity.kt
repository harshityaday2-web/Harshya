package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val sender: String, // "USER" or "LUNA"
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String? = null, // e.g. "WHATSAPP", "YOUTUBE", "ALARM", "ICT_CONCEPT", "WEB_SEARCH"
    val actionPayload: String? = null
)
