package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun observeUser(email: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE isDeleted = 0 ORDER BY isAdmin DESC, email ASC")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isDeleted = 1 ORDER BY email ASC")
    fun getDeletedUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE isDeleted = 0")
    fun observeActiveUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE isDeleted = 1")
    fun observeDeletedUserCount(): Flow<Int>

    @Query("UPDATE users SET isDeleted = 1 WHERE email = :email")
    suspend fun softDeleteUser(email: String)

    @Query("UPDATE users SET isDeleted = 0 WHERE email = :email")
    suspend fun restoreUser(email: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
