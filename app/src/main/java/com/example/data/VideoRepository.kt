package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VideoRepository(context: Context) {
    private val database = HalaanDatabase.getDatabase(context)
    private val videoDao = database.videoDao()
    private val commentDao = database.commentDao()
    private val userDao = database.userDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (videoDao.getCount() == 0) {
                val initialVideos = PreloadedData.getInitialVideos()
                videoDao.insertAll(initialVideos)
                initialVideos.take(3).forEach { video ->
                    PreloadedData.getInitialComments(video.id).forEach { comment ->
                        commentDao.insertComment(comment)
                    }
                }
            }

            // Seed initial admin user if not present
            if (userDao.getUserByEmail("aydinmilani17@gmail.com") == null) {
                userDao.insertUser(
                    UserEntity(
                        email = "aydinmilani17@gmail.com",
                        name = "آیدین میلانی (مدیر ارشد)",
                        password = "Kurdmilani121211",
                        bio = "مدیریت کل سیستم و سرپرست محتوای پلتفرم حلا تی‌وی",
                        avatarColorHex = 0xFFFF2B4EL,
                        joinDate = "شهریور ۱۴۰۵",
                        isAdmin = true,
                        isDeleted = false
                    )
                )
            }

            // Seed a few initial active community members if user count is low
            if (userDao.getUserCount() <= 1) {
                val seedMembers = listOf(
                    UserEntity(
                        email = "reza.rahimi@halaantv.ir",
                        name = "رضا رحیمی",
                        password = "user1234",
                        bio = "منتقد فیلم و فیلمساز مستقل، عاشق سبک نئونوآر",
                        avatarColorHex = 0xFF3B82F6L,
                        joinDate = "شهریور ۱۴۰۵",
                        isAdmin = false,
                        isDeleted = false
                    ),
                    UserEntity(
                        email = "sara.kaviani@halaantv.ir",
                        name = "سارا کاویانی",
                        password = "user1234",
                        bio = "تدوین‌گر ویدیو و طراح جلوه‌های صوتی",
                        avatarColorHex = 0xFF8B5CF6L,
                        joinDate = "شهریور ۱۴۰۵",
                        isAdmin = false,
                        isDeleted = false
                    ),
                    UserEntity(
                        email = "mohammad.alizadeh@halaantv.ir",
                        name = "محمد علیزاده",
                        password = "user1234",
                        bio = "مستندساز طبیعت و عاشقان فناوری‌های تصویری",
                        avatarColorHex = 0xFF10B981L,
                        joinDate = "شهریور ۱۴۰۵",
                        isAdmin = false,
                        isDeleted = false
                    )
                )
                seedMembers.forEach { userDao.insertUser(it) }
            }
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    fun observeUser(email: String): Flow<UserEntity?> = userDao.observeUser(email)

    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    fun getAllActiveUsers(): Flow<List<UserEntity>> = userDao.getAllActiveUsers()

    fun getDeletedUsers(): Flow<List<UserEntity>> = userDao.getDeletedUsers()

    fun observeActiveUserCount(): Flow<Int> = userDao.observeActiveUserCount()

    fun observeDeletedUserCount(): Flow<Int> = userDao.observeDeletedUserCount()

    suspend fun softDeleteUser(email: String) {
        userDao.softDeleteUser(email)
    }

    suspend fun restoreUser(email: String) {
        userDao.restoreUser(email)
    }

    fun getAllVideos(): Flow<List<VideoEntity>> = videoDao.getAllVideos()
    fun getLongVideos(): Flow<List<VideoEntity>> = videoDao.getLongVideos()
    fun getShortVideos(): Flow<List<VideoEntity>> = videoDao.getShortVideos()
    fun getReels(): Flow<List<VideoEntity>> = videoDao.getReels()
    fun getFeaturedVideo(): Flow<VideoEntity?> = videoDao.getFeaturedVideo()
    fun getVideoById(id: String): Flow<VideoEntity?> = videoDao.getVideoById(id)
    fun getDownloadedVideos(): Flow<List<VideoEntity>> = videoDao.getDownloadedVideos()
    fun getBookmarkedVideos(): Flow<List<VideoEntity>> = videoDao.getBookmarkedVideos()
    fun getHistoryVideos(): Flow<List<VideoEntity>> = videoDao.getHistoryVideos()
    fun searchVideos(query: String): Flow<List<VideoEntity>> = videoDao.searchVideos(query)

    suspend fun insertVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun updateVideo(video: VideoEntity) {
        videoDao.updateVideo(video)
    }

    suspend fun deleteVideo(video: VideoEntity) {
        videoDao.deleteVideo(video)
    }

    suspend fun toggleLike(video: VideoEntity) {
        val newLiked = !video.isLiked
        val newLikesCount = if (newLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        videoDao.updateLikeStatus(video.id, newLiked, newLikesCount)
    }

    suspend fun toggleBookmark(video: VideoEntity) {
        videoDao.updateBookmarkStatus(video.id, !video.isBookmarked)
    }

    suspend fun toggleDownload(video: VideoEntity) {
        val newDownloaded = !video.isDownloaded
        val progress = if (newDownloaded) 1f else 0f
        videoDao.updateDownloadStatus(video.id, newDownloaded, progress)
    }

    suspend fun updateWatchProgress(videoId: String, seconds: Int) {
        videoDao.updateWatchProgress(videoId, seconds)
    }

    suspend fun recordView(videoId: String) {
        videoDao.incrementViews(videoId)
    }

    fun getComments(videoId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(videoId: String, authorName: String, text: String) {
        commentDao.insertComment(
            CommentEntity(
                videoId = videoId,
                authorName = authorName,
                text = text,
                timeAgo = "لحظاتی پیش",
                avatarColorHex = 0xFFFF2B4EL
            )
        )
    }
}
