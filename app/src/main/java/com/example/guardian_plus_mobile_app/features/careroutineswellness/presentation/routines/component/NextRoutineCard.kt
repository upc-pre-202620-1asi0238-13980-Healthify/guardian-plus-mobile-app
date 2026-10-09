package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Reminder
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderStatus
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.detailLine
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.displayTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.iconRes
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.timeUntil
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.formatClockTime
import java.time.Duration
import java.time.Instant

/** "14:00 · EN 25 MIN · Losartán 50 mg": the next pending routine of the day, tinted in pastel yellow. */
@Composable
fun NextRoutineCard(
    modifier: Modifier = Modifier,
    reminder: Reminder,
    now: Instant,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val detail = reminder.detailLine()

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = scheme.surface,
        border = BorderStroke(1.dp, scheme.outline)
    ) {
        Row(
            modifier = Modifier
                // Yellow fading into white within the first third, as in the prototype
                .background(Brush.horizontalGradient(0f to scheme.noticeContainer, 0.3f to scheme.surface))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = formatClockTime(reminder.scheduledTime),
                    style = MaterialTheme.typography.dataLabel,
                    color = scheme.onNoticeContainer
                )
                Text(
                    text = timeUntil(reminder.scheduledTime, now),
                    style = MaterialTheme.typography.dataLabel.copy(fontSize = 9.sp, lineHeight = 12.sp),
                    color = scheme.onNoticeContainer
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(scheme.noticeContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(reminder.type.iconRes()),
                    contentDescription = null,
                    tint = scheme.onNoticeContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.displayTitle(), style = MaterialTheme.typography.titleSmall, color = scheme.onSurface)
                if (detail.isNotEmpty()) {
                    Text(text = detail, style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = scheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NextRoutineCardPreview() {
    val now = Instant.parse("2026-10-08T18:35:00Z")
    GuardianTheme(dynamicColor = false) {
        NextRoutineCard(
            reminder = Reminder(
                id = "1",
                personUnderCareId = "elena",
                type = ReminderType.MEDICATION,
                title = "Losartán 50 mg",
                dosage = "1 tableta",
                instructions = "Después del almuerzo",
                scheduledTime = now + Duration.ofMinutes(25),
                status = ReminderStatus.SCHEDULED
            ),
            now = now,
            onClick = {}
        )
    }
}
