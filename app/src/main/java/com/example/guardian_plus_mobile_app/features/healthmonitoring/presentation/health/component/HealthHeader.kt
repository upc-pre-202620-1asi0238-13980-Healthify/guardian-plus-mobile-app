package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge

/** Top bar of the Health section: who is being watched, whether the wearable is online and the overall state. */
@Composable
fun HealthHeader(
    modifier: Modifier = Modifier,
    userInitials: String,
    careRecipientName: String,
    hasLiveSignal: Boolean,
    allWithinRange: Boolean,
    hasReadings: Boolean
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (hasLiveSignal) R.string.health_subtitle_online else R.string.health_subtitle_offline,
                        careRecipientName
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.health_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            // Nothing to judge until the first readings arrive
            if (hasReadings) {
                ReadingBadge(withinRange = allWithinRange)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview
@Composable
private fun HealthHeaderPreview() {
    GuardianTheme(dynamicColor = false) {
        HealthHeader(
            userInitials = "MR",
            careRecipientName = "Elena Rojas",
            hasLiveSignal = true,
            allWithinRange = true,
            hasReadings = true
        )
    }
}
