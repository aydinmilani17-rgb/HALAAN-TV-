package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.data.VideoEntity
import com.example.ui.components.TallVideoCard
import com.example.ui.components.WideVideoCard
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

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    favoriteVideos: List<VideoEntity>,
    historyVideos: List<VideoEntity>,
    recommendedVideos: List<VideoEntity>,
    isSyncing: Boolean,
    lastSyncMessage: String,
    onLogin: (String, String) -> Boolean,
    onRegister: (String, String, String) -> Boolean,
    onUpdateProfile: (String, String) -> Unit,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit,
    onNavigateToStudio: () -> Unit = {},
    onVideoClick: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Dialog states
    var showAuthDialog by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Auth input fields
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    // Edit profile inputs
    var editName by remember { mutableStateOf(currentUser?.name ?: "") }
    var editBio by remember { mutableStateOf(currentUser?.bio ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HalaanBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "پروفایل کاربری و هویت",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HalaanTextPrimary
                        )
                        Text(
                            text = if (currentUser != null) "مدیریت حساب کاربری و سوابق" else "وارد شوید یا حساب کاربری جدید بسازید",
                            fontSize = 12.sp,
                            color = HalaanTextSecondary
                        )
                    }

                    if (currentUser == null) {
                        Button(
                            onClick = {
                                isRegisterMode = false
                                authError = null
                                showAuthDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("ورود / ثبت‌نام", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(
                            onClick = { onLogout() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(HalaanSurface)
                                .border(1.dp, HalaanBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "خروج",
                                tint = Color(0xFFFF453A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Profile Identity Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, HalaanBorder, RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1F1C2B), Color(0xFF14131B))
                        )
                    )
                    .padding(20.dp)
            ) {
                if (currentUser != null) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentUser.isAdmin) HalaanBrandGradient
                                        else Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (currentUser.isAdmin) Icons.Default.Security else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser.name,
                                        color = HalaanTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (currentUser.isAdmin) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x3322C55E))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("مدیر ارشد", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = currentUser.email,
                                    color = HalaanTextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    editName = currentUser.name
                                    editBio = currentUser.bio
                                    showEditProfileDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "ویرایش",
                                    tint = HalaanSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = currentUser.bio,
                            color = HalaanTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        if (currentUser.isAdmin) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToStudio,
                                colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "ورود به پنل استودیو و مدیریت فیلم‌ها",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Guest state prompt
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = HalaanTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "کاربر مهمان",
                            color = HalaanTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "برای ذخیره موارد دلخواه و دریافت پیشنهادات اختصاصی وارد شوید.",
                            color = HalaanTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    isRegisterMode = false
                                    authError = null
                                    showAuthDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary)
                            ) {
                                Text("ورود", color = Color.White)
                            }
                            Button(
                                onClick = {
                                    isRegisterMode = true
                                    authError = null
                                    showAuthDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HalaanSurfaceVariant)
                            ) {
                                Text("ثبت‌نام کاربر جدید", color = HalaanTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Offline-First & Sync Status Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, HalaanBorder, RoundedCornerShape(20.dp))
                        .background(HalaanSurface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSyncing) Color(0x33FFA028) else Color(0x3322C55E)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        color = HalaanSecondary,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = Color(0xFF22C55E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "پایگاه آفلاین و همگام‌سازی",
                                    color = HalaanTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lastSyncMessage,
                                    color = HalaanTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = onSyncNow,
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = HalaanSurfaceVariant)
                        ) {
                            Text(
                                text = if (isSyncing) "در حال هماهنگی..." else "همگام‌سازی",
                                color = HalaanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Recommended Content Engine ("پیشنهادات بر اساس علایق و تاریخچه")
        if (recommendedVideos.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(HalaanBrandGradient)
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HalaanSecondary
                        )
                        Text(
                            text = "پیشنهاد اختصاصی به شما",
                            color = HalaanTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        items(recommendedVideos) { video ->
                            WideVideoCard(
                                video = video,
                                onWatchClick = { onVideoClick(video) },
                                onBookmarkToggle = {}
                            )
                        }
                    }
                }
            }
        }

        // Section: Saved / Favorite Items ("موارد نشان‌شده و دلخواه")
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(HalaanBrandGradient)
                    )
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = HalaanPrimary
                    )
                    Text(
                        text = "فیلم‌های ذخیره شده و دلخواه",
                        color = HalaanTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${favoriteVideos.size})",
                        color = HalaanTextMuted,
                        fontSize = 12.sp
                    )
                }

                if (favoriteVideos.isEmpty()) {
                    Text(
                        text = "هنوز فیلمی به موارد دلخواه اضافه نکرده‌اید.",
                        color = HalaanTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        items(favoriteVideos) { video ->
                            WideVideoCard(
                                video = video,
                                onWatchClick = { onVideoClick(video) },
                                onBookmarkToggle = {}
                            )
                        }
                    }
                }
            }
        }

        // Section: Activity History ("تاریخچه بازدیدها و فعالیت")
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(HalaanBrandGradient)
                    )
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color(0xFF22C55E)
                    )
                    Text(
                        text = "تاریخچه فعالیت و مشاهده‌ها",
                        color = HalaanTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${historyVideos.size})",
                        color = HalaanTextMuted,
                        fontSize = 12.sp
                    )
                }

                if (historyVideos.isEmpty()) {
                    Text(
                        text = "هنوز ویدیویی تماشا نشده است.",
                        color = HalaanTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        items(historyVideos) { video ->
                            WideVideoCard(
                                video = video,
                                onWatchClick = { onVideoClick(video) },
                                onBookmarkToggle = {}
                            )
                        }
                    }
                }
            }
        }
    }

    // Login / Register Modal Dialog
    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            containerColor = HalaanSurface,
            title = {
                Text(
                    text = if (isRegisterMode) "ثبت‌نام کاربر جدید" else "ورود به حساب کاربری",
                    color = HalaanTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("نام و نام خانوادگی", color = HalaanTextMuted, fontSize = 12.sp) },
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
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("ایمیل / جیمیل", color = HalaanTextMuted, fontSize = 12.sp) },
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
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("رمز عبور", color = HalaanTextMuted, fontSize = 12.sp) },
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

                    if (authError != null) {
                        Text(text = authError ?: "", color = Color(0xFFFF453A), fontSize = 12.sp)
                    }

                    // Mode switch toggle
                    TextButton(
                        onClick = {
                            isRegisterMode = !isRegisterMode
                            authError = null
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = if (isRegisterMode) "قبلاً ثبت‌نام کرده‌اید؟ ورود" else "حساب ندارید؟ ثبت‌نام کنید",
                            color = HalaanSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            val ok = onRegister(nameInput, emailInput, passwordInput)
                            if (ok) {
                                showAuthDialog = false
                                Toast.makeText(context, "ثبت‌نام با موفقیت انجام شد!", Toast.LENGTH_SHORT).show()
                            } else {
                                authError = "لطفاً تمام فیلدها را پر کنید."
                            }
                        } else {
                            val ok = onLogin(emailInput, passwordInput)
                            if (ok) {
                                showAuthDialog = false
                                Toast.makeText(context, "ورود با موفقیت انجام شد!", Toast.LENGTH_SHORT).show()
                            } else {
                                authError = "اطلاعات ورود نامعتبر است."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary)
                ) {
                    Text(if (isRegisterMode) "تکمیل ثبت‌نام" else "ورود به حساب", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text("انصراف", color = HalaanTextSecondary)
                }
            }
        )
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = HalaanSurface,
            title = {
                Text("ویرایش اطلاعات کاربری", color = HalaanTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("نام نمایشی", color = HalaanTextMuted, fontSize = 12.sp) },
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
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("بیوگرافی و توضیحات", color = HalaanTextMuted, fontSize = 12.sp) },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editBio)
                        showEditProfileDialog = false
                        Toast.makeText(context, "اطلاعات به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HalaanPrimary)
                ) {
                    Text("ذخیره تغییرات", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("انصراف", color = HalaanTextSecondary)
                }
            }
        )
    }
}
