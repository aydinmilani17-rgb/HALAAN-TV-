package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.VideoEntity
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

@Composable
fun LibraryScreen(
    downloadedVideos: List<VideoEntity>,
    bookmarkedVideos: List<VideoEntity>,
    historyVideos: List<VideoEntity>,
    onVideoClick: (VideoEntity) -> Unit,
    onDeleteDownload: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Downloads, 1: Bookmarks, 2: History

    val totalDownloadedBytes = remember(downloadedVideos) {
        downloadedVideos.sumOf { it.fileSizeBytes }
    }

    val activeList = when (selectedTab) {
        0 -> downloadedVideos
        1 -> bookmarkedVideos
        else -> historyVideos
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HalaanBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("library_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "کتابخانه و دانلودها",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HalaanTextPrimary
                )
                Text(
                    text = "مدیریت فایل‌های ذخیره شده و پخش آفلاین ویدیوها",
                    fontSize = 12.sp,
                    color = HalaanTextSecondary
                )
            }
        }

        // Offline Storage Gauge Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, HalaanBorder, RoundedCornerShape(24.dp))
                    .background(HalaanSurface)
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2222C55E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "حافظه اشغال شده توسط حلا",
                                    color = HalaanTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${Formatters.formatFileSize(totalDownloadedBytes)} برای ${downloadedVideos.size} ویدیو",
                                    color = HalaanTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(HalaanSurfaceVariant)
                                .clickable {
                                    Toast.makeText(context, "کش موقت پخش ویدیوها پاکسازی شد.", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "پاکسازی کش",
                                color = HalaanTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { (downloadedVideos.size / 20f).coerceIn(0.1f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF22C55E),
                        trackColor = HalaanSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "وضعیت: آماده برای پخش ۱۰۰٪ بدون اینترنت",
                            color = Color(0xFF22C55E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "فضای باقی‌مانده آزاد",
                            color = HalaanTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Filter Pills Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Triple(0, "دانلودها (${downloadedVideos.size})", Icons.Default.CheckCircle),
                    Triple(1, "نشان‌شده‌ها (${bookmarkedVideos.size})", Icons.Default.Bookmark),
                    Triple(2, "تاریخچه (${historyVideos.size})", Icons.Default.History)
                )

                tabs.forEach { (index, title, icon) ->
                    val isSelected = selectedTab == index
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) HalaanBrandGradient else Brush.linearGradient(listOf(HalaanSurface, HalaanSurface)))
                            .border(1.dp, if (isSelected) Color.Transparent else HalaanBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedTab = index }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("library_tab_$index")
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else HalaanTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else HalaanTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Empty state
        if (activeList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = HalaanTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "موردی در این بخش یافت نشد",
                            color = HalaanTextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ویدیوها را از بخش خانه یا پخش تماشا کرده و ذخیره نمایید.",
                            color = HalaanTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            // Video List Items
            items(activeList) { video ->
                val imageResId = rememberDrawableRes(context, video.drawableResName, R.drawable.hero_cinema_showcase)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                        .background(HalaanSurface)
                        .clickable { onVideoClick(video) }
                        .padding(12.dp)
                        .testTag("library_item_${video.id}")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HalaanSurfaceVariant)
                        ) {
                            if (imageResId != null) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = video.title,
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
                                    text = Formatters.formatDuration(video.durationSeconds),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Meta details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                color = HalaanTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.channelName,
                                color = HalaanTextSecondary,
                                fontSize = 11.sp
                            )

                            if (selectedTab == 2 && video.watchProgressSeconds > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (video.watchProgressSeconds.toFloat() / video.durationSeconds).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = HalaanPrimary,
                                    trackColor = HalaanSurfaceVariant
                                )
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = Formatters.formatFileSize(video.fileSizeBytes),
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(text = "•", color = HalaanTextMuted)
                                    Text(
                                        text = "آماده تماشا",
                                        color = Color(0xFF22C55E),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Actions (Play or Delete)
                        if (selectedTab == 0) {
                            IconButton(
                                onClick = { onDeleteDownload(video) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "حذف دانلود",
                                    tint = HalaanTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HalaanPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "تماشا",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
