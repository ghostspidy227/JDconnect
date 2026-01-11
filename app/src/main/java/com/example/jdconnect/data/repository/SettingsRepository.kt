package com.example.jdconnect.data.repository

import android.content.Context
import com.example.jdconnect.model.AppSettings

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("jdconnect_settings", Context.MODE_PRIVATE)

    fun getSettings(): AppSettings {
        val heartbeatMinutes = prefs.getInt("heartbeat_minutes", 30)
        val graceMultiplier = prefs.getString("grace_multiplier", 1.5.toString())?.toDoubleOrNull() ?: 1.5
        val monitoringEnabled = prefs.getBoolean("heartbeat_monitoring_enabled", true)

        return AppSettings(
            heartbeatMonitoringEnabled = monitoringEnabled,
            defaultHeartbeatMinutes = heartbeatMinutes,
            graceMultiplier = graceMultiplier
        )
    }

    fun updateSettings(settings: AppSettings) {
        prefs.edit()
            .putBoolean("heartbeat_monitoring_enabled", settings.heartbeatMonitoringEnabled)
            .putInt("heartbeat_minutes", settings.defaultHeartbeatMinutes)
            .putString("grace_multiplier", settings.graceMultiplier.toString())
            .apply()
    }
}
