package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus

/** Rounded label for a state ("Nueva", "En atención", "Notificada"…), optionally with a check mark. */
@Composable
fun StatusChip(
    modifier: Modifier = Modifier,
    text: String,
    container: Color,
    content: Color,
    showCheck: Boolean = false
) {
    Row(
        modifier = modifier
            .background(container, MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showCheck) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

// Semantic colors of each state, following the state label table of the report (section 3.1.2.2)

@Composable
fun AlertStatus.colors(): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        AlertStatus.PENDING_CONFIRMATION -> scheme.noticeContainer to scheme.onNoticeContainer
        AlertStatus.TRIGGERED, AlertStatus.ESCALATED -> scheme.errorContainer to scheme.onErrorContainer
        AlertStatus.ACKNOWLEDGED -> scheme.tertiaryContainer to scheme.tertiary
        AlertStatus.DISMISSED, AlertStatus.RESOLVED -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
}

@Composable
fun IncidentStatus.colors(): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        IncidentStatus.IN_ATTENTION -> scheme.tertiaryContainer to scheme.tertiary
        IncidentStatus.STABILIZED -> scheme.primaryContainer to scheme.onPrimaryContainer
        IncidentStatus.CLOSED -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
}

@Preview
@Composable
private fun StatusChipPreview() {
    GuardianTheme(dynamicColor = false) {
        val (container, content) = AlertStatus.RESOLVED.colors()
        StatusChip(text = "Resuelta", container = container, content = content)
    }
}
