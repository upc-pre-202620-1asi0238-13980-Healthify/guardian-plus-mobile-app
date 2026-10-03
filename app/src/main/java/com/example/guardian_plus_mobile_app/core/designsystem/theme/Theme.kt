package com.example.guardian_plus_mobile_app.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// The report defines a single light palette ("a calm home, not a hospital"), so there is no dark scheme yet
private val GuardianColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = PrimaryForeground,
    primaryContainer = SecondaryPastel,
    onPrimaryContainer = SecondaryForeground,
    secondary = SecondaryForeground,
    onSecondary = PrimaryForeground,
    secondaryContainer = AccentMint,
    onSecondaryContainer = AccentForeground,
    tertiary = PastelOrangeText,
    onTertiary = PrimaryForeground,
    tertiaryContainer = PastelOrange,
    onTertiaryContainer = PastelOrangeDeep,
    error = Destructive,
    onError = DestructiveForeground,
    errorContainer = DestructivePastel,
    onErrorContainer = Destructive,
    background = NeutralBackground,
    onBackground = NeutralForeground,
    surface = SurfaceCard,
    onSurface = NeutralForeground,
    surfaceVariant = NeutralMuted,
    onSurfaceVariant = MutedForeground,
    surfaceContainer = NeutralMuted,
    surfaceContainerHigh = NeutralMuted,
    outline = Border,
    outlineVariant = Border
)

// Radius tokens (report, section 3.1.1.1 D): subtle 6, default 12, large 20
private val GuardianShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun GuardianTheme(
    // Off by default: the brand palette carries meaning (green is "all good", red is an emergency)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(LocalContext.current)
    } else {
        GuardianColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = GuardianShapes,
        content = content
    )
}
