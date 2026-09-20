package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val name: String,
    val password: String, // stored locally for authentication
    val bio: String = "علاقه‌مند به سینما و مستند در حلا تی‌وی",
    val avatarColorHex: Long = 0xFFFF2B4EL,
    val joinDate: String = "شهریور ۱۴۰۵",
    val isAdmin: Boolean = false,
    val isDeleted: Boolean = false
)
