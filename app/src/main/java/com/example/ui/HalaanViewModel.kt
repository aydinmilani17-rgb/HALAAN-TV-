package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CommentEntity
import com.example.data.UserEntity
import com.example.data.VideoEntity
import com.example.data.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab {
    HOME,
    REELS,
    LIBRARY,
    STUDIO,
    PROFILE
}

class HalaanViewModel(application: Application) : AndroidViewModel(application) {
    val repository = VideoRepository(application)

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _showIntro = MutableStateFlow(true)
    val showIntro: StateFlow<Boolean> = _showIntro.asStateFlow()

    private val _selectedVideo = MutableStateFlow<VideoEntity?>(null)
    val selectedVideo: StateFlow<VideoEntity?> = _selectedVideo.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("همه")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Current Authenticated User state
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val isAdmin: StateFlow<Boolean> = _currentUser.map { user ->
        user != null && user.email.equals("aydinmilani17@gmail.com", ignoreCase = true) && user.isAdmin
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Offline Syncing status
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("هم‌اکنون پایگاه محلی فعال است")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    val allVideos: StateFlow<List<VideoEntity>> = repository.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeUsers: StateFlow<List<UserEntity>> = repository.getAllActiveUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedUsers: StateFlow<List<UserEntity>> = repository.getDeletedUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeUserCount: StateFlow<Int> = repository.observeActiveUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val deletedUserCount: StateFlow<Int> = repository.observeDeletedUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longVideos: StateFlow<List<VideoEntity>> = repository.getLongVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortVideos: StateFlow<List<VideoEntity>> = repository.getShortVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels: StateFlow<List<VideoEntity>> = repository.getReels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredVideo: StateFlow<VideoEntity?> = repository.getFeaturedVideo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val downloadedVideos: StateFlow<List<VideoEntity>> = repository.getDownloadedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedVideos: StateFlow<List<VideoEntity>> = repository.getBookmarkedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyVideos: StateFlow<List<VideoEntity>> = repository.getHistoryVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recommendation Engine: based on categories and tags of favorited / bookmarked / viewed videos
    val recommendedVideos: StateFlow<List<VideoEntity>> = combine(
        allVideos,
        bookmarkedVideos,
        historyVideos
    ) { all, bookmarks, history ->
        val interacted = (bookmarks + history).distinctBy { it.id }
        if (interacted.isEmpty()) {
            all.take(4)
        } else {
            val preferredCategories = interacted.map { it.category }.toSet()
            val interactedIds = interacted.map { it.id }.toSet()

            val matching = all.filter { it.category in preferredCategories && it.id !in interactedIds }
            if (matching.isNotEmpty()) {
                matching.take(6)
            } else {
                all.filter { it.id !in interactedIds }.take(4)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentComments: StateFlow<List<CommentEntity>> = _selectedVideo.flatMapLatest { video ->
        if (video != null) repository.getComments(video.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(email: String, pass: String): Boolean {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()

        if (trimmedEmail.equals("aydinmilani17@gmail.com", ignoreCase = true) && trimmedPass == "Kurdmilani121211") {
            val adminUser = UserEntity(
                email = "aydinmilani17@gmail.com",
                name = "آیدین میلانی",
                password = trimmedPass,
                bio = "مدیر کل و سرپرست محتوای حلا تی‌وی",
                avatarColorHex = 0xFFFF2B4EL,
                joinDate = "شهریور ۱۴۰۵",
                isAdmin = true
            )
            _currentUser.value = adminUser
            viewModelScope.launch { repository.saveUser(adminUser) }
            return true
        }

        var success = false
        viewModelScope.launch {
            val existing = repository.getUserByEmail(trimmedEmail)
            if (existing != null && existing.password == trimmedPass) {
                _currentUser.value = existing
                success = true
            }
        }
        return success
    }

    fun register(name: String, email: String, pass: String): Boolean {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        val trimmedName = name.trim()
        if (trimmedEmail.isBlank() || trimmedPass.isBlank() || trimmedName.isBlank()) return false

        val isAdminAccount = trimmedEmail.equals("aydinmilani17@gmail.com", ignoreCase = true)
        val newUser = UserEntity(
            email = trimmedEmail,
            name = trimmedName,
            password = trimmedPass,
            bio = if (isAdminAccount) "مدیر کل و سرپرست محتوای حلا تی‌وی" else "عضو پلتفرم حلا تی‌وی",
            avatarColorHex = if (isAdminAccount) 0xFFFF2B4EL else 0xFFFFA028L,
            joinDate = "شهریور ۱۴۰۵",
            isAdmin = isAdminAccount
        )
        viewModelScope.launch {
            repository.saveUser(newUser)
            _currentUser.value = newUser
        }
        return true
    }

    fun updateProfile(name: String, bio: String) {
        val curr = _currentUser.value ?: return
        val updated = curr.copy(name = name.trim(), bio = bio.trim())
        _currentUser.value = updated
        viewModelScope.launch {
            repository.updateUser(updated)
        }
    }

    fun logout() {
        _currentUser.value = null
        if (_currentTab.value == NavigationTab.STUDIO) {
            _currentTab.value = NavigationTab.HOME
        }
    }

    fun syncOfflineData() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // simulate syncing local delta and verifying cache integrity
            _lastSyncTime.value = "همگام‌سازی با موفقیت انجام شد"
            _isSyncing.value = false
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun dismissIntro() {
        _showIntro.value = false
    }

    fun openVideo(video: VideoEntity) {
        _selectedVideo.value = video
        viewModelScope.launch {
            repository.recordView(video.id)
        }
    }

    fun closeVideo() {
        _selectedVideo.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video)
            if (_selectedVideo.value?.id == video.id) {
                val updatedLiked = !video.isLiked
                val updatedCount = if (updatedLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
                _selectedVideo.value = video.copy(isLiked = updatedLiked, likesCount = updatedCount)
            }
        }
    }

    fun toggleBookmark(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(video)
            if (_selectedVideo.value?.id == video.id) {
                _selectedVideo.value = video.copy(isBookmarked = !video.isBookmarked)
            }
        }
    }

    fun toggleDownload(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleDownload(video)
            if (_selectedVideo.value?.id == video.id) {
                _selectedVideo.value = video.copy(isDownloaded = !video.isDownloaded)
            }
        }
    }

    fun addComment(videoId: String, authorName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(videoId, authorName.ifBlank { "کاربر حلا" }, text.trim())
        }
    }

    fun addVideo(
        title: String,
        description: String,
        category: String,
        channelName: String,
        durationMinutes: Int,
        durationSeconds: Int,
        type: String // "LONG", "SHORT", "REEL"
    ) {
        viewModelScope.launch {
            val totalSeconds = (durationMinutes * 60) + durationSeconds
            val id = "hal_usr_" + UUID.randomUUID().toString().take(8)
            val newVideo = VideoEntity(
                id = id,
                title = title,
                description = description,
                category = category,
                channelName = channelName.ifBlank { "کانال من" },
                durationSeconds = totalSeconds.coerceAtLeast(15),
                viewsCount = 1,
                likesCount = 0,
                isLiked = false,
                isBookmarked = false,
                isDownloaded = true,
                downloadProgress = 1f,
                fileSizeBytes = (totalSeconds * 250_000L).coerceAtLeast(15_000_000L),
                inLong = type == "LONG",
                inShort = type == "SHORT",
                inReels = type == "REEL",
                isFeatured = false,
                drawableResName = if (type == "REEL") "hero_reel_neon" else "hero_cinema_showcase",
                gradientStartHex = 0xFFFF2B4EL,
                gradientEndHex = 0xFFFFA028L,
                createdAt = System.currentTimeMillis()
            )
            repository.insertVideo(newVideo)
        }
    }

    fun deleteVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.deleteVideo(video)
            if (_selectedVideo.value?.id == video.id) {
                _selectedVideo.value = null
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        // Prevent deleting primary admin account
        if (user.email.equals("aydinmilani17@gmail.com", ignoreCase = true)) return
        viewModelScope.launch {
            repository.softDeleteUser(user.email)
        }
    }

    fun restoreUser(user: UserEntity) {
        viewModelScope.launch {
            repository.restoreUser(user.email)
        }
    }

    fun updateWatchProgress(seconds: Int) {
        val current = _selectedVideo.value ?: return
        viewModelScope.launch {
            repository.updateWatchProgress(current.id, seconds)
            _selectedVideo.value = current.copy(watchProgressSeconds = seconds)
        }
    }
}
