package com.example.guardian_plus_mobile_app.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Guardian+ palette (report, section 3.1.1.1 B)
val Primary = Color(0xFF167A62)
val PrimaryForeground = Color(0xFFFFFFFF)
val SecondaryPastel = Color(0xFFD3F8F0)
val SecondaryForeground = Color(0xFF169084)
val AccentMint = Color(0xFFD9F0E7)
val AccentForeground = Color(0xFF123128)
val NeutralBackground = Color(0xFFF8FBF9)
val NeutralForeground = Color(0xFF123128)
val NeutralMuted = Color(0xFFEEF4F1)
val MutedForeground = Color(0xFF587168)
val Border = Color(0xFFCFE0D8)
val SurfaceCard = Color(0xFFFFFFFF)
val Destructive = Color(0xFFC53B3B)
val DestructiveForeground = Color(0xFFFFFFFF)
val DestructivePastel = Color(0xFFFBE4E4)
val PastelOrange = Color(0xFFFFE6CC)
val PastelOrangeText = Color(0xFFB25900)
val PastelOrangeDeep = Color(0xFF7A3E00)
val PastelYellow = Color(0xFFFFF4CC)
val PastelYellowText = Color(0xFF806600)

// Notice (pastel yellow) roles. Material 3 has no slot for a second warning level, so they are
// exposed as ColorScheme extensions and still read as MaterialTheme.colorScheme.noticeContainer.
val ColorScheme.noticeContainer: Color get() = PastelYellow
val ColorScheme.onNoticeContainer: Color get() = PastelYellowText

// Category tints of the routines prototype (appointments and hydration in blue, sleep in violet)
val PastelBlue = Color(0xFFE1F4F7)
val PastelBlueText = Color(0xFF24798B)
val PastelViolet = Color(0xFFEBE8F8)
val PastelVioletText = Color(0xFF5D4D8C)

val ColorScheme.infoContainer: Color get() = PastelBlue
val ColorScheme.onInfoContainer: Color get() = PastelBlueText
val ColorScheme.restContainer: Color get() = PastelViolet
val ColorScheme.onRestContainer: Color get() = PastelVioletText

// Login screen colors (Primary is defined above)
val BrandGreen = Color(0xFF2F6A57)      // header, primary button
val BrandGreenDark = Color(0xFF27594A)  // gradient bottom
val SheetBg = Color(0xFFF7F9F8)         // off-white sheet
val FieldBorder = Color(0xFFE3E8E5)
val TextPrimary = Color(0xFF1E2C27)
val TextMuted = Color(0xFF6C7B75)
val LabelCaps = Color(0xFF5A6B64)
val MintBg = Color(0xFFD9F0E5)          // biometric button above
