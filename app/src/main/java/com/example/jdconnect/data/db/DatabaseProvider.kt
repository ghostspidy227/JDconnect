package com.example.jdconnect.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: build(context).also { instance = it }
        }
    }

    private fun build(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "jdconnect.db"
        )
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration()  // For development: drop and recreate on migration failure
            .build()
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE servers ADD COLUMN lastHeartbeatAt INTEGER"
            )
            database.execSQL(
                "ALTER TABLE servers ADD COLUMN isOnline INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE servers ADD COLUMN notificationsEnabled INTEGER NOT NULL DEFAULT 1"
            )
        }
    }
}
