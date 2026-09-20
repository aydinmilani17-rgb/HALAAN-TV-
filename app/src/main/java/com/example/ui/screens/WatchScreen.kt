package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CommentEntity
import com.example.data.VideoEntity
import com.example.ui.components.WideVideoCard
import com.example.ui.components.rememberDrawableRes
import com.example.ui.theme.HalaanBackground
import com.example.ui.theme.HalaanBorder
import com.example.ui.theme.HalaanBrandGradient
import com.example.ui.theme.HalaanPrimary
import com.example.ui.theme.HalaanSecondary
import com.example.ui.theme.HalaanSurface
import com.example.ui.theme.HalaanSurfaceHighlight
import com.example.ui.theme.HalaanSurfaceVariant
import com.example.ui.theme.HalaanTextMuted
import com.example.ui.theme.HalaanTextPrimary
import com.example.ui.theme.HalaanTextSecondary
import com.example.utils.Formatters
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen(
    video: VideoEntity,
    relatedVideos: List<VideoEntity>,
    comments: List<CommentEntity>,
    onBackClick: () -> Unit,
    onLikeToggle: (VideoEntity) -> Unit,
    onBookmarkToggle: (VideoEntity) -> Unit,
    onDownloadToggle: (VideoEntity) -> Unit,
    onVideoSelect: (VideoEntity) -> Unit,
    onAddComment: (String, String, String) -> Unit,
    onUpdateProgress: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = rememberDrawableRes(context, video.drawableResName, R.drawable.hero_cinema_showcase)

    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableIntStateOf(video.watchProgressSeconds.coerceAtLeast(0)) }
    var showControls by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var newCommentText by remember { mutableStateOf("") }
    var isSubscribed by remember { mutableStateOf(false) }

    // Simulated playback timer
    LaunchedEffect(isPlaying, playbackSpeed) {
        while (isPlaying) {
            val delayMs = (1000 / playbackSpeed).toLong()
            delay(delayMs)
            if (currentSeconds < video.durationSeconds) {
                currentSeconds += 1
                onUpdateProgress(currentSeconds)
            } else {
                isPlaying = false
            }
        }
    }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HalaanBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("watch_screen")
    ) {
        // Player Box (16:9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
                .clickable { showControls = !showControls }
        ) {
            // Video Frame
            if (imageResId != null) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(video.gradientStartHex), Color(video.gradientEndHex))
                            )
                        )
                )
            }

            // Dark vignette overlay when controls are visible
            if (showControls) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000))
                )

                // Top Controls: Back button and Video Quality
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x66000000), CircleShape)
                            .testTag("watch_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Playback Speed Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x77000000))
                                .clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        2.0f -> 0.75f
                                        else -> 1.0f
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Offline 1080p Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HalaanPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "1080p آفلاین",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Center Play/Pause & Skip Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // -10s
                    IconButton(
                        onClick = { currentSeconds = (currentSeconds - 10).coerceAtLeast(0) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x77000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "۱۰ ثانیه عقب",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Play/Pause
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(HalaanBrandGradient)
                            .testTag("player_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "توقف" else "پخش",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // +10s
                    IconButton(
                        onClick = { currentSeconds = (currentSeconds + 10).coerceAtMost(video.durationSeconds) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x77000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "۱۰ ثانیه جلو",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bottom Progress Scrubber & Times
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Formatters.formatDuration(currentSeconds),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Formatters.formatDuration(video.durationSeconds),
                            color = Color(0xCCFFFFFF),
                            fontSize = 11.sp
                        )
                    }

                    Slider(
                        value = currentSeconds.toFloat(),
                        onValueChange = { currentSeconds = it.toInt() },
                        valueRange = 0f..video.durationSeconds.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = HalaanPrimary,
                            activeTrackColor = HalaanPrimary,
                            inactiveTrackColor = Color(0x66FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .testTag("player_progress_slider")
                    )
                }
            }
        }

        // Scrollable Info & Content Area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Video Title & Meta
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = video.title,
                        color = HalaanTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "${Formatters.formatViews(video.viewsCount)} بازدید",
                            color = HalaanTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(text = "•", color = HalaanTextMuted)
                        Text(
                            text = video.category,
                            color = HalaanPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "•", color = HalaanTextMuted)
                        Text(
                            text = "آفلاین آماده",
                            color = Color(0xFF22C55E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Like
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { onLikeToggle(video) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(HalaanSurfaceVariant, CircleShape)
                                    .testTag("watch_like_button")
                            ) {
                                Icon(
                                    imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "پسندیدن",
                                    tint = if (video.isLiked) HalaanPrimary else HalaanTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Formatters.formatViews(video.likesCount),
                                color = HalaanTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Download / Offline Saved
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    onDownloadToggle(video)
                                    Toast.makeText(
                                        context,
                                        if (video.isDownloaded) "ویدیو از حافظه آفلاین حذف شد" else "ویدیو در حافظه آفلاین ذخیره شد",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (video.isDownloaded) Color(0x2222C55E) else HalaanSurfaceVariant, CircleShape)
                                    .testTag("watch_download_button")
                            ) {
                                Icon(
                                    imageVector = if (video.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                    contentDescription = "دانلود آفلاین",
                                    tint = if (video.isDownloaded) Color(0xFF22C55E) else HalaanTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (video.isDownloaded) "ذخیره شد" else "دانلود",
                                color = if (video.isDownloaded) Color(0xFF22C55E) else HalaanTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Bookmark
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { onBookmarkToggle(video) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(HalaanSurfaceVariant, CircleShape)
                                    .testTag("watch_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "نشان کردن",
                                    tint = if (video.isBookmarked) HalaanSecondary else HalaanTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "نشان",
                                color = HalaanTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Share
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "اشتراک‌گذاری ویدیو حلا تی‌وی", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(HalaanSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "اشتراک‌گذاری",
                                    tint = HalaanTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "اشتراک",
                                color = HalaanTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Channel Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(HalaanSurface)
                            .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(HalaanBrandGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = video.channelName.take(1),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = video.channelName,
                                    color = HalaanTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${video.subscribersCount} دنبال‌کننده",
                                    color = HalaanTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSubscribed) HalaanSurfaceHighlight else HalaanPrimary)
                                .clickable { isSubscribed = !isSubscribed }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = if (isSubscribed) "عضو شدید" else "دنبال کردن",
                                color = if (isSubscribed) HalaanTextSecondary else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tab Row (Related Videos, Comments, Description)
            item {
                val tabTitles = listOf("ویدیوهای مشابه", "دیدگاه‌ها (${comments.size})", "مشخصات و سرفصل‌ها")
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = HalaanBackground,
                    contentColor = HalaanPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = HalaanPrimary
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) HalaanTextPrimary else HalaanTextMuted
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Related Videos
                    val otherVideos = relatedVideos.filter { it.id != video.id }
                    if (otherVideos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ویدیوی دیگری در این بخش وجود ندارد.", color = HalaanTextMuted, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(otherVideos) { itemVideo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onVideoSelect(itemVideo) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val itemImageRes = rememberDrawableRes(context, itemVideo.drawableResName, R.drawable.hero_tech_banner)
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(HalaanSurface)
                                ) {
                                    if (itemImageRes != null) {
                                        Image(
                                            painter = painterResource(id = itemImageRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xCC000000))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = Formatters.formatDuration(itemVideo.durationSeconds),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = itemVideo.title,
                                        color = HalaanTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = itemVideo.channelName,
                                        color = HalaanTextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${Formatters.formatViews(itemVideo.viewsCount)} بازدید",
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Comments Tab
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Add Comment Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("دیدگاه خود را درباره این ویدیو بنویسید...", fontSize = 12.sp, color = HalaanTextMuted) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = HalaanSurface,
                                        unfocusedContainerColor = HalaanSurface,
                                        focusedBorderColor = HalaanPrimary,
                                        unfocusedBorderColor = HalaanBorder,
                                        focusedTextColor = HalaanTextPrimary,
                                        unfocusedTextColor = HalaanTextPrimary
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = {
                                        if (newCommentText.isNotBlank()) {
                                            onAddComment(video.id, "کاربر حلا", newCommentText)
                                            newCommentText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(HalaanBrandGradient)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "ارسال",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (comments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("دیدگاهی ثبت نشده است. اولین دیدگاه را شما بنویسید!", color = HalaanTextMuted, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(comments) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(comment.avatarColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = comment.authorName.take(1),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = comment.authorName,
                                                color = HalaanTextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = comment.timeAgo,
                                                color = HalaanTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = comment.text,
                                            color = HalaanTextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Details & Chapters
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "درباره ویدیو:",
                                color = HalaanTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = video.description,
                                color = HalaanTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "سرفصل‌های ویدیو (تایم‌لاین):",
                                color = HalaanTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val chapters = listOf(
                                Pair(0, "مقدمه و معرفی مبحث"),
                                Pair((video.durationSeconds * 0.25).toInt(), "بررسی نکات کلیدی و تجزیه تحلیل"),
                                Pair((video.durationSeconds * 0.6).toInt(), "دمو و مقایسه عملکرد"),
                                Pair((video.durationSeconds * 0.85).toInt(), "جمع‌بندی و نتیجه‌گیری")
                            )

                            chapters.forEach { chapter ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { currentSeconds = chapter.first }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(HalaanPrimary.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = Formatters.formatDuration(chapter.first),
                                            color = HalaanPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = chapter.second,
                                        color = HalaanTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
