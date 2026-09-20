package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HalaanBottomNav
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IntroScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.WatchScreen
import com.example.ui.theme.HalaanBackground

@Composable
fun HalaanApp(
    viewModel: HalaanViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showIntro by viewModel.showIntro.collectAsState()
    val selectedVideo by viewModel.selectedVideo.collectAsState()

    val longVideos by viewModel.longVideos.collectAsState()
    val shortVideos by viewModel.shortVideos.collectAsState()
    val reels by viewModel.reels.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val downloadedVideos by viewModel.downloadedVideos.collectAsState()
    val bookmarkedVideos by viewModel.bookmarkedVideos.collectAsState()
    val historyVideos by viewModel.historyVideos.collectAsState()
    val recommendedVideos by viewModel.recommendedVideos.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    val activeUsers by viewModel.activeUsers.collectAsState()
    val deletedUsers by viewModel.deletedUsers.collectAsState()
    val activeUserCount by viewModel.activeUserCount.collectAsState()
    val deletedUserCount by viewModel.deletedUserCount.collectAsState()

    val currentComments by viewModel.currentComments.collectAsState()

    // Back handling
    BackHandler(enabled = selectedVideo != null || currentTab != NavigationTab.HOME) {
        if (selectedVideo != null) {
            viewModel.closeVideo()
        } else if (currentTab != NavigationTab.HOME) {
            viewModel.selectTab(NavigationTab.HOME)
        }
    }

    if (showIntro) {
        IntroScreen(
            onEnterClick = { viewModel.dismissIntro() }
        )
    } else if (selectedVideo != null) {
        val video = selectedVideo!!
        WatchScreen(
            video = video,
            relatedVideos = allVideos,
            comments = currentComments,
            onBackClick = { viewModel.closeVideo() },
            onLikeToggle = { viewModel.toggleLike(it) },
            onBookmarkToggle = { viewModel.toggleBookmark(it) },
            onDownloadToggle = { viewModel.toggleDownload(it) },
            onVideoSelect = { viewModel.openVideo(it) },
            onAddComment = { vId, author, text -> viewModel.addComment(vId, author, text) },
            onUpdateProgress = { seconds -> viewModel.updateWatchProgress(seconds) }
        )
    } else {
        Scaffold(
            bottomBar = {
                HalaanBottomNav(
                    currentTab = currentTab,
                    isAdmin = isAdmin,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            containerColor = HalaanBackground,
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            longVideos = longVideos,
                            shortVideos = shortVideos,
                            recommendedVideos = recommendedVideos,
                            onVideoClick = { viewModel.openVideo(it) },
                            onBookmarkToggle = { viewModel.toggleBookmark(it) },
                            onLikeToggle = { viewModel.toggleLike(it) }
                        )
                    }
                    NavigationTab.REELS -> {
                        ReelsScreen(
                            reels = reels,
                            allVideos = allVideos,
                            onLikeToggle = { viewModel.toggleLike(it) },
                            onBookmarkToggle = { viewModel.toggleBookmark(it) },
                            comments = currentComments,
                            onAddComment = { vId, author, text -> viewModel.addComment(vId, author, text) },
                            onWatchClick = { viewModel.openVideo(it) }
                        )
                    }
                    NavigationTab.LIBRARY -> {
                        LibraryScreen(
                            downloadedVideos = downloadedVideos,
                            bookmarkedVideos = bookmarkedVideos,
                            historyVideos = historyVideos,
                            onVideoClick = { viewModel.openVideo(it) },
                            onDeleteDownload = { viewModel.toggleDownload(it) }
                        )
                    }
                    NavigationTab.STUDIO -> {
                        if (isAdmin) {
                            StudioScreen(
                                currentUser = currentUser,
                                isAdmin = isAdmin,
                                videos = allVideos,
                                activeUsers = activeUsers,
                                deletedUsers = deletedUsers,
                                activeUserCount = activeUserCount,
                                deletedUserCount = deletedUserCount,
                                onLoginClick = { email, pass -> viewModel.login(email, pass) },
                                onAddVideo = { t, d, c, ch, m, s, type ->
                                    viewModel.addVideo(t, d, c, ch, m, s, type)
                                },
                                onDeleteVideo = { viewModel.deleteVideo(it) },
                                onVideoClick = { viewModel.openVideo(it) },
                                onDeleteUser = { viewModel.deleteUser(it) },
                                onRestoreUser = { viewModel.restoreUser(it) }
                            )
                        } else {
                            // Non-admin fallback redirects to Home
                            HomeScreen(
                                longVideos = longVideos,
                                shortVideos = shortVideos,
                                recommendedVideos = recommendedVideos,
                                onVideoClick = { viewModel.openVideo(it) },
                                onBookmarkToggle = { viewModel.toggleBookmark(it) },
                                onLikeToggle = { viewModel.toggleLike(it) }
                            )
                        }
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            currentUser = currentUser,
                            favoriteVideos = bookmarkedVideos,
                            historyVideos = historyVideos,
                            recommendedVideos = recommendedVideos,
                            isSyncing = isSyncing,
                            lastSyncMessage = lastSyncTime,
                            onLogin = { email, pass -> viewModel.login(email, pass) },
                            onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                            onUpdateProfile = { name, bio -> viewModel.updateProfile(name, bio) },
                            onLogout = { viewModel.logout() },
                            onSyncNow = { viewModel.syncOfflineData() },
                            onNavigateToStudio = { viewModel.selectTab(NavigationTab.STUDIO) },
                            onVideoClick = { viewModel.openVideo(it) }
                        )
                    }
                }
            }
        }
    }
}
