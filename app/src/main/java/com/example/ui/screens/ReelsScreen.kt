package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ui.components.rememberDrawableRes
import com.example.ui.theme.HalaanBackground
import com.example.ui.theme.HalaanBorder
import com.example.ui.theme.HalaanBrandGradient
import com.example.ui.theme.HalaanPrimary
import com.example.ui.theme.HalaanSecondary
import com.example.ui.theme.HalaanSurface
import com.example.ui.theme.HalaanSurfaceVariant
import com.example.ui.theme.HalaanTextMuted
import com.example.ui.theme.HalaanTextPrimary
import com.example.ui.theme.HalaanTextSecondary
import com.example.utils.Formatters

@Composable
fun ReelsScreen(
    reels: List<VideoEntity>,
    allVideos: List<VideoEntity> = emptyList(),
    onLikeToggle: (VideoEntity) -> Unit,
    onBookmarkToggle: (VideoEntity) -> Unit,
    comments: List<CommentEntity>,
    onAddComment: (String, String, String) -> Unit,
    onWatchClick: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(HalaanBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "هنوز ریلزی بارگذاری نشده است",
                color = HalaanTextSecondary,
                fontSize = 14.sp
            )
        }
        return
    }

    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { reels.size })
    var isMuted by remember { mutableStateOf(false) }
    var activeCommentsReelId by remember { mutableStateOf<String?>(null) }
    var newCommentText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            ReelItemView(
                reel = reel,
                isMuted = isMuted,
                onToggleMute = { isMuted = !isMuted },
                onLikeToggle = { onLikeToggle(reel) },
                onBookmarkToggle = { onBookmarkToggle(reel) },
                onOpenComments = { activeCommentsReelId = reel.id },
                onShare = {
                    Toast.makeText(context, "لینک ریلز کپی شد: ${reel.title}", Toast.LENGTH_SHORT).show()
                },
                onWatchDetail = {
                    val targetVideo = if (!reel.linkedVideoId.isNullOrBlank()) {
                        allVideos.find { it.id == reel.linkedVideoId } ?: reel
                    } else {
                        reel
                    }
                    onWatchClick(targetVideo)
                }
            )
        }

        // Comments Bottom Sheet
        AnimatedVisibility(
            visible = activeCommentsReelId != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val currentReelId = activeCommentsReelId ?: ""
            val reelComments = comments.filter { it.videoId == currentReelId }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(HalaanSurface)
                    .border(1.dp, HalaanBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دیدگاه‌ها (${reelComments.size})",
                            color = HalaanTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { activeCommentsReelId = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = HalaanTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (reelComments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "اولین نفری باشید که دیدگاهی ثبت می‌کند!",
                                        color = HalaanTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            items(reelComments) { comment ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(comment.avatarColorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = comment.authorName.take(1),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add comment input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("دیدگاه خود را بنویسید...", fontSize = 12.sp, color = HalaanTextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = HalaanSurfaceVariant,
                                unfocusedContainerColor = HalaanSurfaceVariant,
                                focusedBorderColor = HalaanPrimary,
                                unfocusedBorderColor = HalaanBorder,
                                focusedTextColor = HalaanTextPrimary,
                                unfocusedTextColor = HalaanTextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    onAddComment(currentReelId, "کاربر حلا", newCommentText)
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
        }
    }
}

@Composable
fun ReelItemView(
    reel: VideoEntity,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onLikeToggle: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onWatchDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = rememberDrawableRes(context, reel.drawableResName, R.drawable.hero_reel_neon)
    var isPlaying by remember { mutableStateOf(true) }

    // Pulsing visual bar for simulated playback
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { isPlaying = !isPlaying }
    ) {
        // Video backdrop image
        if (imageResId != null) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = reel.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = pulseAlpha }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(reel.gradientStartHex), Color(reel.gradientEndHex))
                        )
                    )
            )
        }

        // Overlay vignette gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99000000),
                            Color.Transparent,
                            Color(0xCC000000)
                        )
                    )
                )
        )

        // Play/Pause indicator when paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "پخش",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Top Mute button & Channel Tag
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HalaanPrimary)
                )
                Text(
                    text = "HALAAN REELS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x88000000))
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = "صدا",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Side action buttons column (Right side or Left side according to design)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLikeToggle,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .testTag("reel_like_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "پسندیدن",
                        tint = if (reel.isLiked) HalaanPrimary else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = Formatters.formatViews(reel.likesCount),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onOpenComments,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .testTag("reel_comment_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ModeComment,
                        contentDescription = "دیدگاه",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "نظر",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

            // Bookmark / Save
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .testTag("reel_bookmark_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = if (reel.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "ذخیره",
                        tint = if (reel.isBookmarked) HalaanSecondary else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "نشان",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "اشتراک‌گذاری",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "اشتراک",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }

        // Bottom Details & Creator info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.8f)
                .padding(start = 20.dp, bottom = 100.dp)
        ) {
            // Channel row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(HalaanBrandGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.channelName.take(1),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "@" + reel.channelName.replace(" ", "_"),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HalaanPrimary)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "دنبال کردن",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reel.description,
                color = Color(0xDDFFFFFF),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Watch Full Movie button if linked to long or short video
            if (!reel.linkedVideoId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(HalaanBrandGradient)
                        .clickable { onWatchDetail() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("reel_watch_full_movie_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "تماشای فیلم کامل",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "تماشای فیلم کامل",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio track tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x55000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = HalaanSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "صدای اصلی ${reel.channelName} • آفلاین",
                    color = Color(0xEEFFFFFF),
                    fontSize = 10.sp
                )
            }
        }
    }
}
