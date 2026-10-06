package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryItemEntity::class,
        TradingNoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun lunaDao(): LunaDao

    companion object {
        @Volatile
        private var INSTANCE: LunaDatabase? = null

        fun getDatabase(context: Context): LunaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LunaDatabase::class.java,
                    "luna_assistant_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
