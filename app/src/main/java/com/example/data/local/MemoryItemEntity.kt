package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_items")
data class MemoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String, // "PREFERENCE", "COMMAND", "APP", "MUSIC", "TRADING"
    val timestamp: Long = System.currentTimeMillis()
)
