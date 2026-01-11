package com.example.jdconnect.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jdconnect.data.dao.EventDao
import com.example.jdconnect.data.dao.ServerDao
import com.example.jdconnect.data.entity.EventEntity
import com.example.jdconnect.data.entity.ServerEntity

@Database(
    entities = [ServerEntity::class, EventEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun eventDao(): EventDao
}
