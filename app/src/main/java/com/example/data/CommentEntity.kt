package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: String,
    val authorName: String,
    val text: String,
    val likesCount: Int = 0,
    val timeAgo: String = "به تازگی",
    val avatarColorHex: Long = 0xFFFF2B4EL,
    val createdAt: Long = System.currentTimeMillis()
)
