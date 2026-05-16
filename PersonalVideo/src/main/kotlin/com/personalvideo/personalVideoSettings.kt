package com.personalvideo

import android.content.Context

object PersonalVideoSettings {
    private const val PREFS_NAME = "personal_video_settings"
    private const val KEY_BASE_URL = "base_url"

    const val DEFAULT_BASE_URL = "http://127.0.0.1:8080"

    fun getBaseUrl(context: Context): String {
        val saved = preferences(context).getString(KEY_BASE_URL, null).orEmpty()
        return normalizeBaseUrl(saved).ifBlank { DEFAULT_BASE_URL }
    }

    fun saveBaseUrl(context: Context, value: String): String {
        val baseUrl = normalizeBaseUrl(value).ifBlank { DEFAULT_BASE_URL }
        preferences(context).edit()
            .putString(KEY_BASE_URL, baseUrl)
            .apply()
        return baseUrl
    }

    fun resetBaseUrl(context: Context): String {
        preferences(context).edit()
            .remove(KEY_BASE_URL)
            .apply()
        return DEFAULT_BASE_URL
    }

    fun normalizeBaseUrl(value: String): String {
        val trimmed = value.trim().trimEnd('/')
        if (trimmed.isBlank()) return ""
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            return trimmed
        }
        return "http://$trimmed"
    }

    private fun preferences(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
