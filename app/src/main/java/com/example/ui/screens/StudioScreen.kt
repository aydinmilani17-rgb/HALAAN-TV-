package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.data.VideoEntity
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
fun StudioScreen(
    currentUser: UserEntity?,
    isAdmin: Boolean,
    videos: List<VideoEntity>,
    activeUsers: List<UserEntity> = emptyList(),
    deletedUsers: List<UserEntity> = emptyList(),
    activeUserCount: Int = 0,
    deletedUserCount: Int = 0,
    onLoginClick: (String, String) -> Boolean,
    onAddVideo: (String, String, String, String, Int, Int, String) -> Unit,
    onDeleteVideo: (VideoEntity) -> Unit,
    onVideoClick: (VideoEntity) -> Unit,
    onDeleteUser: (UserEntity) -> Unit = {},
    onRestoreUser: (UserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStudioTab by remember { mutableIntStateOf(0) } // 0 = ویدیوها, 1 = اعضای فعال, 2 = سطل زباله (حذف شده‌ها)

    // Admin login form state
    var emailInput by remember { mutableStateOf("aydinmilani17@gmail.com") }
    var passwordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Dialog state for adding video
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var channelName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("سینما و فیلم") }
    var selectedType by remember { mutableStateOf("LONG") } // LONG, SHORT, REEL
    var minutes by remember { mutableStateOf("4") }
    var seconds by remember { mutableStateOf("30") }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            if (title.isBlank()) {
                title = "ویدیوی انتخابی گالری"
            }
            Toast.makeText(context, "ویدیو از گالری انتخاب شد", Toast.LENGTH_SHORT).show()
        }
    }

    val totalViews = remember(videos) { videos.sumOf { it.viewsCount.toLong() } }
    val totalLikes = remember(videos) { videos.sumOf { it.likesCount.toLong() } }

    // IF NOT ADMIN -> SHOW SECURE ACCESS LOCK
    if (!isAdmin) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(HalaanBackground)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, HalaanBorder, RoundedCornerShape(24.dp))
                    .background(HalaanSurface)
                    .padding(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(HalaanBrandGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "قفل پنل ادمین",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ورود به پنل مدیریت و ادمین",
                    color = HalaanTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "دسترسی به بخش مدیریت و استودیو فقط برای جیمیل مدیر ارشد مجاز است.",
                    color = HalaanTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        loginError = null
                    },
                    label = { Text("جیمیل مدیر", fontSize = 12.sp, color = HalaanTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = HalaanSurfaceVariant,
                        unfocusedContainerColor = HalaanSurfaceVariant,
                        focusedBorderColor = HalaanPrimary,
                        unfocusedBorderColor = HalaanBorder,
                        focusedTextColor = HalaanTextPrimary,
                        unfocusedTextColor = HalaanTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        loginError = null
                    },
                    label = { Text("رمز عبور اختصاصی ادمین", fontSize = 12.sp, color = HalaanTextMuted) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = HalaanSurfaceVariant,
                        unfocusedContainerColor = HalaanSurfaceVariant,
                        focusedBorderColor = HalaanPrimary,
                        unfocusedBorderColor = HalaanBorder,
                        focusedTextColor = HalaanTextPrimary,
                        unfocusedTextColor = HalaanTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (loginError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginError ?: "",
                        color = Color(0xFFFF453A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val success = onLoginClick(emailInput, passwordInput)
                        if (success) {
                            Toast.makeText(context, "خوش آمدید مدیر ارشد!", Toast.LENGTH_SHORT).show()
                        } else {
                            loginError = "ایمیل یا رمز عبور ادمین نادرست است."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Text("احراز هویت و ورود", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // IF ADMIN IS LOGGED IN -> SHOW FULL MANAGEMENT STUDIO
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HalaanBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("studio_screen_admin"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "پنل مدیریت ادمین",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HalaanTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x3322C55E))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("فعال", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "مدیر: aydinmilani17@gmail.com",
                            fontSize = 12.sp,
                            color = HalaanSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Add Video Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(HalaanBrandGradient)
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("open_add_video_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "افزودن",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "افزودن فیلم",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Stats Cards Row
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Views Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                            .background(HalaanSurface)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = HalaanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = "مجموع بازدیدها", fontSize = 11.sp, color = HalaanTextMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Formatters.formatViews(totalViews.toInt()),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = HalaanTextPrimary
                            )
                        }
                    }

                    // Total Likes Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                            .background(HalaanSurface)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = HalaanSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = "مجموع پسندها", fontSize = 11.sp, color = HalaanTextMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Formatters.formatViews(totalLikes.toInt()),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = HalaanTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Cards Row 2: Active Users & Deleted Users Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Active Users Count Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                            .background(HalaanSurface)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = "تعداد کل کاربران فعال", fontSize = 11.sp, color = HalaanTextMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$activeUserCount کاربر",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = HalaanTextPrimary
                            )
                        }
                    }

                    // Deleted Users (Trash) Count Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                            .background(HalaanSurface)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = Color(0xFFFF453A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(text = "سطل زباله کاربران", fontSize = 11.sp, color = HalaanTextMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$deletedUserCount حذف‌شده",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (deletedUserCount > 0) Color(0xFFFF453A) else HalaanTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Videos Count Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                        .background(HalaanSurface)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ویدیوهای فعال در دیتابیس:",
                                color = HalaanTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "${videos.size} فیلم",
                            color = HalaanTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Tabs Row for Management
        item {
            TabRow(
                selectedTabIndex = selectedStudioTab,
                containerColor = HalaanBackground,
                contentColor = HalaanPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedStudioTab]),
                        color = HalaanPrimary,
                        height = 3.dp
                    )
                },
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedStudioTab == 0,
                    onClick = { selectedStudioTab = 0 },
                    text = {
                        Text(
                            text = "فیلم‌ها (${videos.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedStudioTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedStudioTab == 0) HalaanPrimary else HalaanTextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedStudioTab == 1,
                    onClick = { selectedStudioTab = 1 },
                    text = {
                        Text(
                            text = "کاربران فعال ($activeUserCount)",
                            fontSize = 13.sp,
                            fontWeight = if (selectedStudioTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedStudioTab == 1) HalaanPrimary else HalaanTextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedStudioTab == 2,
                    onClick = { selectedStudioTab = 2 },
                    text = {
                        Text(
                            text = "حذف شده‌ها ($deletedUserCount)",
                            fontSize = 13.sp,
                            fontWeight = if (selectedStudioTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedStudioTab == 2) Color(0xFFFF453A) else HalaanTextMuted
                        )
                    }
                )
            }
        }

        // TAB 0: Videos List
        if (selectedStudioTab == 0) {
            if (videos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("هیچ ویدیویی یافت نشد", color = HalaanTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(videos) { video ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(18.dp))
                            .background(HalaanSurface)
                            .clickable { onVideoClick(video) }
                            .padding(12.dp)
                            .testTag("studio_video_row_${video.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(HalaanSurfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (video.inShort) "فیلم کوتاه" else if (video.inReels) "ریلز" else "فیلم بلند",
                                            color = HalaanPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = video.category,
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = video.title,
                                    color = HalaanTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = video.channelName,
                                        color = HalaanTextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(text = "•", color = HalaanTextMuted)
                                    Text(
                                        text = "${Formatters.formatViews(video.viewsCount)} بازدید",
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(text = "•", color = HalaanTextMuted)
                                    Text(
                                        text = Formatters.formatDuration(video.durationSeconds),
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Delete button
                            IconButton(
                                onClick = {
                                    onDeleteVideo(video)
                                    Toast.makeText(context, "فیلم با موفقیت حذف شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف ویدیو",
                                    tint = Color(0xFFFF453A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: Active Users List with Soft Delete Action
        if (selectedStudioTab == 1) {
            if (activeUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("کاربر فعالی ثبت نشده است", color = HalaanTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(activeUsers) { user ->
                    val isPrimaryAdmin = user.email.equals("aydinmilani17@gmail.com", ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, HalaanBorder, RoundedCornerShape(18.dp))
                            .background(HalaanSurface)
                            .padding(12.dp)
                            .testTag("active_user_row_${user.email}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(user.avatarColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = user.name,
                                            color = HalaanTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (user.isAdmin) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFFF2B4E).copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "مدیر",
                                                    color = HalaanPrimary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = user.email,
                                        color = HalaanTextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = user.bio,
                                        color = HalaanTextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (!isPrimaryAdmin) {
                                IconButton(
                                    onClick = {
                                        onDeleteUser(user)
                                        Toast.makeText(context, "کاربر ${user.name} به بخش حذف‌شده‌ها منتقل شد", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("delete_user_btn_${user.email}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonRemove,
                                        contentDescription = "حذف کاربر و انتقال به سطل زباله",
                                        tint = Color(0xFFFF453A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(HalaanSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("غیرقابل حذف", color = HalaanTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: Trash / Deleted Users List with Restore Action
        if (selectedStudioTab == 2) {
            if (deletedUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = HalaanTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "سطل زباله کاربران خالی است",
                                color = HalaanTextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(deletedUsers) { user ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0xFFFF453A).copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                            .background(HalaanSurface)
                            .padding(12.dp)
                            .testTag("deleted_user_row_${user.email}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.take(1),
                                        color = Color.LightGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = user.name,
                                            color = HalaanTextSecondary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFFF453A).copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "حذف شده",
                                                color = Color(0xFFFF453A),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = user.email,
                                        color = HalaanTextMuted,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Restore Button
                            Button(
                                onClick = {
                                    onRestoreUser(user)
                                    Toast.makeText(context, "کاربر ${user.name} با موفقیت به برنامه بازگردانده شد", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22C55E),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("restore_user_btn_${user.email}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = "بازگردانی کاربر",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "بازگردانی",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Video Dialog
    if (showAddDialog) {
        val categories = listOf("سینما و فیلم", "فناوری و هوش مصنوعی", "مستند علمی", "آموزش و توسعه", "گیمینگ و استریم")

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = HalaanSurface,
            title = {
                Text(
                    text = "افزودن فیلم جدید (ادمین)",
                    color = HalaanTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Gallery Video Picker Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                if (selectedVideoUri != null) Color(0xFF22C55E) else HalaanBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .background(HalaanSurfaceVariant)
                            .clickable {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedVideoUri != null) Icons.Default.Movie else Icons.Default.Add,
                                contentDescription = "انتخاب از گالری",
                                tint = if (selectedVideoUri != null) Color(0xFF22C55E) else HalaanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedVideoUri != null) "ویدیو از حافظه/گالری متصل شد" else "انتخاب فایل ویدیو از گالری گوشی",
                                    color = if (selectedVideoUri != null) Color(0xFF22C55E) else HalaanTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (selectedVideoUri != null) selectedVideoUri.toString() else "برای انتخاب فایل ویدیویی دستگاه ضربه بزنید",
                                    color = HalaanTextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان فیلم", color = HalaanTextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = HalaanSurfaceVariant,
                            unfocusedContainerColor = HalaanSurfaceVariant,
                            focusedBorderColor = HalaanPrimary,
                            unfocusedBorderColor = HalaanBorder,
                            focusedTextColor = HalaanTextPrimary,
                            unfocusedTextColor = HalaanTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = channelName,
                        onValueChange = { channelName = it },
                        label = { Text("نام کارگردان / کانال", color = HalaanTextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = HalaanSurfaceVariant,
                            unfocusedContainerColor = HalaanSurfaceVariant,
                            focusedBorderColor = HalaanPrimary,
                            unfocusedBorderColor = HalaanBorder,
                            focusedTextColor = HalaanTextPrimary,
                            unfocusedTextColor = HalaanTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("خلاصه داستان / توضیحات", color = HalaanTextMuted, fontSize = 12.sp) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = HalaanSurfaceVariant,
                            unfocusedContainerColor = HalaanSurfaceVariant,
                            focusedBorderColor = HalaanPrimary,
                            unfocusedBorderColor = HalaanBorder,
                            focusedTextColor = HalaanTextPrimary,
                            unfocusedTextColor = HalaanTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Type Selector: Long, Short, or Reel
                    Text("نوع ویدیو:", color = HalaanTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(
                            Pair("LONG", "فیلم بلند"),
                            Pair("SHORT", "فیلم کوتاه"),
                            Pair("REEL", "ریلز عمودی")
                        ).forEach { (typeCode, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { selectedType = typeCode }
                            ) {
                                RadioButton(
                                    selected = selectedType == typeCode,
                                    onClick = { selectedType = typeCode },
                                    colors = RadioButtonDefaults.colors(selectedColor = HalaanPrimary)
                                )
                                Text(text = label, color = HalaanTextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val minVal = minutes.toIntOrNull() ?: 5
                            val secVal = seconds.toIntOrNull() ?: 0
                            onAddVideo(
                                title.trim(),
                                description.trim().ifBlank { "فیلم جدید در پلتفرم حلا تی‌وی" },
                                selectedCategory,
                                channelName.trim().ifBlank { "استودیو فیلم حلا" },
                                minVal,
                                secVal,
                                selectedType
                            )
                            showAddDialog = false
                            title = ""
                            description = ""
                            channelName = ""
                            selectedVideoUri = null
                            Toast.makeText(context, "فیلم با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary)
                ) {
                    Text("انتشار در حلا تی‌وی", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    selectedVideoUri = null
                }) {
                    Text("انصراف", color = HalaanTextSecondary)
                }
            }
        )
    }
}
