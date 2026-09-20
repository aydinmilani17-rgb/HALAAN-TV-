package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoEntity
import com.example.ui.components.TallVideoCard
import com.example.ui.components.WideVideoCard
import com.example.ui.theme.HalaanBackground
import com.example.ui.theme.HalaanBrandGradient
import com.example.ui.theme.HalaanPrimary
import com.example.ui.theme.HalaanSecondary
import com.example.ui.theme.HalaanSurface
import com.example.ui.theme.HalaanSurfaceVariant
import com.example.ui.theme.HalaanTextMuted
import com.example.ui.theme.HalaanTextPrimary
import com.example.ui.theme.HalaanTextSecondary

@Composable
fun HomeScreen(
    longVideos: List<VideoEntity>,
    shortVideos: List<VideoEntity>,
    recommendedVideos: List<VideoEntity> = emptyList(),
    onVideoClick: (VideoEntity) -> Unit,
    onBookmarkToggle: (VideoEntity) -> Unit,
    onLikeToggle: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HalaanBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("home_screen_clean"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Section 1: Long Videos ("فیلم‌های بلند")
        item {
            Column(modifier = Modifier.padding(bottom = 20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(HalaanBrandGradient)
                    )
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = HalaanPrimary
                    )
                    Text(
                        text = "فیلم‌های بلند",
                        color = HalaanTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "(${longVideos.size})",
                        color = HalaanTextMuted,
                        fontSize = 13.sp
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(longVideos) { video ->
                        WideVideoCard(
                            video = video,
                            onWatchClick = { onVideoClick(video) },
                            onBookmarkToggle = { onBookmarkToggle(video) }
                        )
                    }
                }
            }
        }

        // Section 2: Short Videos ("فیلم‌های کوتاه")
        item {
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(HalaanBrandGradient)
                    )
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = HalaanSecondary
                    )
                    Text(
                        text = "فیلم‌های کوتاه",
                        color = HalaanTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "(${shortVideos.size})",
                        color = HalaanTextMuted,
                        fontSize = 13.sp
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(shortVideos) { video ->
                        TallVideoCard(
                            video = video,
                            onWatchClick = { onVideoClick(video) },
                            onLikeToggle = { onLikeToggle(video) }
                        )
                    }
                }
            }
        }
    }
}
