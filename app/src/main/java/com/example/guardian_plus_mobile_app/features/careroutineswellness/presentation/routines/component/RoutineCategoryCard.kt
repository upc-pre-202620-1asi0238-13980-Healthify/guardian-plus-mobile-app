package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.colors
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutineCategory
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.RoutineCategorySummary
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip

/** One row of "Todas las rutinas": tinted icon, name, today's summary and an optional badge. */
@Composable
fun RoutineCategoryCard(
    modifier: Modifier = Modifier,
    category: RoutineCategory,
    summary: RoutineCategorySummary,
    onClick: () -> Unit
) {
    val (iconContainer, iconContent) = category.tone.colors()

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 72.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(category.iconRes),
                    contentDescription = null,
                    tint = iconContent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(category.titleRes),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    summary.badge?.let { badge ->
                        val (badgeContainer, badgeContent) = summary.badgeTone.colors()
                        StatusChip(
                            modifier = Modifier.padding(start = 8.dp),
                            text = badge,
                            container = badgeContainer,
                            content = badgeContent
                        )
                    }
                }
                Text(
                    text = summary.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RoutineCategoryCardPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutineCategoryCard(
            category = RoutineCategory.SLEEP,
            summary = RoutineCategorySummary(detail = "7 h 24 min anoche", badge = "Fragmentado", badgeTone = RoutineTone.ORANGE),
            onClick = {}
        )
    }
}
