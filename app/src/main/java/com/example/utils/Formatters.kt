package com.example.utils

import java.util.Locale

object Formatters {
    fun formatDuration(seconds: Int): String {
        if (seconds <= 0) return "0:00"
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.US, "%d:%02d", m, s)
    }

    fun formatViews(views: Int): String {
        return when {
            views >= 1_000_000 -> String.format(Locale.US, "%.1fM", views / 1_000_000f)
            views >= 1_000 -> String.format(Locale.US, "%.1fK", views / 1_000f)
            else -> views.toString()
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024L * 1024 * 1024 -> String.format(Locale.US, "%.1f گیگابایت", bytes / (1024f * 1024 * 1024))
            else -> String.format(Locale.US, "%.0f مگابایت", bytes / (1024f * 1024))
        }
    }
}
