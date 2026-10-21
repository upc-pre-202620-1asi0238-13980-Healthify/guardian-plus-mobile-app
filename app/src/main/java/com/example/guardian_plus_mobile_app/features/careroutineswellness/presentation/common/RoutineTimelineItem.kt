package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer

/** Where a routine of the day stands, which sets the colors of its dot and border. */
enum class TimelineState {
    DONE,
    CURRENT,
    UPCOMING,
    MISSED
}

/**
 * One block of "Hoy" or "Plan de hoy": time, state dot, title, detail and badge. The current block may carry
 * an action ("Confirmar toma", "Registrar como completada") across the whole card.
 */
@Composable
fun RoutineTimelineItem(
    modifier: Modifier = Modifier,
    time: String,
    @DrawableRes iconRes: Int,
    title: String,
    detail: String,
    badge: String,
    badgeTone: RoutineTone,
    state: TimelineState,
    actionLabel: String? = null,
    @DrawableRes actionIconRes: Int = R.drawable.ic_check,
    isActionLoading: Boolean = false,
    onActionClick: () -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme
    val (dotContainer, dotContent) = when (state) {
        TimelineState.DONE -> scheme.primaryContainer to scheme.primary
        TimelineState.CURRENT -> scheme.noticeContainer to scheme.onNoticeContainer
        TimelineState.UPCOMING -> scheme.surfaceVariant to scheme.onSurfaceVariant
        TimelineState.MISSED -> scheme.tertiaryContainer to scheme.tertiary
    }
    val border = if (state == TimelineState.CURRENT) scheme.onNoticeContainer.copy(alpha = 0.35f) else scheme.outline
    val icon = when (state) {
        TimelineState.DONE -> R.drawable.ic_check
        TimelineState.MISSED -> R.drawable.ic_x
        TimelineState.UPCOMING -> R.drawable.ic_clock
        TimelineState.CURRENT -> iconRes
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = scheme.surface,
        border = BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.dataLabel,
                    color = scheme.onSurface,
                    modifier = Modifier
                        .width(48.dp)
                        .padding(top = 5.dp)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(dotContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(icon), contentDescription = null, tint = dotContent, modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, color = scheme.onSurface)
                    if (detail.isNotEmpty()) {
                        Text(text = detail, style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
                    }
                }
                RoutineBadge(modifier = Modifier.padding(start = 8.dp), text = badge, tone = badgeTone)
            }
            actionLabel?.let { label ->
                Button(
                    onClick = onActionClick,
                    enabled = !isActionLoading,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(44.dp)
                ) {
                    if (isActionLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = scheme.onPrimary
                        )
                    } else {
                        Icon(painter = painterResource(actionIconRes), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(text = label)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RoutineTimelineItemPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutineTimelineItem(
            time = "14:00",
            iconRes = R.drawable.ic_pill,
            title = "Losartán 50 mg",
            detail = "1 tableta · Después del almuerzo",
            badge = "Pendiente",
            badgeTone = RoutineTone.YELLOW,
            state = TimelineState.CURRENT,
            actionLabel = "Confirmar toma"
        )
    }
}
