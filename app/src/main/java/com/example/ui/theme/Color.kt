package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val HalaanPrimary = Color(0xFFFF2B4E)
val HalaanSecondary = Color(0xFFFFA028)
val HalaanTertiary = Color(0xFF7A5AF8)

val HalaanBackground = Color(0xFF0A0A0E)
val HalaanSurface = Color(0xFF14141B)
val HalaanSurfaceVariant = Color(0xFF1E1E28)
val HalaanSurfaceHighlight = Color(0xFF282836)

val HalaanTextPrimary = Color(0xFFF9F9FB)
val HalaanTextSecondary = Color(0xFFA2A2B2)
val HalaanTextMuted = Color(0xFF6B6B7D)
val HalaanBorder = Color(0xFF242432)

val HalaanBrandGradient = Brush.linearGradient(
    colors = listOf(HalaanPrimary, HalaanSecondary)
)

val HalaanCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0x00000000), Color(0xDD0A0A0E))
)

