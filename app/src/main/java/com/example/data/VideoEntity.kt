package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val channelName: String,
    val channelAvatar: String = "",
    val subscribersCount: String = "100K",
    val durationSeconds: Int,
    val viewsCount: Int,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val fileSizeBytes: Long = 85_000_000L,
    val inLong: Boolean = true,
    val inShort: Boolean = false,
    val inReels: Boolean = false,
    val isFeatured: Boolean = false,
    val drawableResName: String = "",
    val linkedVideoId: String? = null,
    val gradientStartHex: Long = 0xFFFF2B4EL,
    val gradientEndHex: Long = 0xFFFFA028L,
    val watchProgressSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
