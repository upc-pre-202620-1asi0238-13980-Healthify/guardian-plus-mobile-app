package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.guardian_plus_mobile_app.core.designsystem.theme.infoContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onInfoContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onRestContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.restContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip

/** Pastel tints of the routines prototype, shared by the category icons and their badges. */
enum class RoutineTone {
    MINT,
    BLUE,
    VIOLET,
    ORANGE,
    YELLOW,
    MUTED,
    RED
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
        RoutineTone.RED -> scheme.errorContainer to scheme.onErrorContainer
    }
}

/** Rounded badge of the prototype ("Tomada", "Stock bajo", "Fragmentado"…) in one of the tints. */
@Composable
fun RoutineBadge(modifier: Modifier = Modifier, text: String, tone: RoutineTone = RoutineTone.MINT) {
    val (container, content) = tone.colors()
    StatusChip(modifier = modifier, text = text, container = container, content = content)
}
