package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.guardian_plus_mobile_app.core.designsystem.theme.infoContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onInfoContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onRestContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.restContainer

/** Pastel tints of the routines prototype, shared by the category icons and their badges. */
enum class RoutineTone {
    MINT,
    BLUE,
    VIOLET,
    ORANGE,
    YELLOW,
    MUTED
}

/** Container and content color of each tint. */
@Composable
fun RoutineTone.colors(): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        RoutineTone.MINT -> scheme.primaryContainer to scheme.primary
        RoutineTone.BLUE -> scheme.infoContainer to scheme.onInfoContainer
        RoutineTone.VIOLET -> scheme.restContainer to scheme.onRestContainer
        RoutineTone.ORANGE -> scheme.tertiaryContainer to scheme.tertiary
        RoutineTone.YELLOW -> scheme.noticeContainer to scheme.onNoticeContainer
        RoutineTone.MUTED -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
}
