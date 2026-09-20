package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE inLong = 1 ORDER BY createdAt DESC")
    fun getLongVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE inShort = 1 ORDER BY createdAt DESC")
    fun getShortVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE inReels = 1 ORDER BY createdAt DESC")
    fun getReels(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isFeatured = 1 LIMIT 1")
    fun getFeaturedVideo(): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: String): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY createdAt DESC")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE watchProgressSeconds > 0 ORDER BY createdAt DESC")
    fun getHistoryVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoEntity>)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :videoId")
    suspend fun updateLikeStatus(videoId: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE videos SET isBookmarked = :isBookmarked WHERE id = :videoId")
    suspend fun updateBookmarkStatus(videoId: String, isBookmarked: Boolean)

    @Query("UPDATE videos SET isDownloaded = :isDownloaded, downloadProgress = :progress WHERE id = :videoId")
    suspend fun updateDownloadStatus(videoId: String, isDownloaded: Boolean, progress: Float)

    @Query("UPDATE videos SET watchProgressSeconds = :seconds WHERE id = :videoId")
    suspend fun updateWatchProgress(videoId: String, seconds: Int)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementViews(videoId: String)
}
