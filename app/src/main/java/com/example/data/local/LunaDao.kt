package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LunaDao {
    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    // Memory Items
    @Query("SELECT * FROM memory_items ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(item: MemoryItemEntity): Long

    @Update
    suspend fun updateMemory(item: MemoryItemEntity)

    @Delete
    suspend fun deleteMemory(item: MemoryItemEntity)

    @Query("DELETE FROM memory_items WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM memory_items")
    suspend fun clearAllMemories()

    // Trading Notes
    @Query("SELECT * FROM trading_notes ORDER BY timestamp DESC")
    fun getAllTradingNotes(): Flow<List<TradingNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradingNote(note: TradingNoteEntity): Long

    @Delete
    suspend fun deleteTradingNote(note: TradingNoteEntity)

    @Query("DELETE FROM trading_notes WHERE id = :id")
    suspend fun deleteTradingNoteById(id: Long)
}
