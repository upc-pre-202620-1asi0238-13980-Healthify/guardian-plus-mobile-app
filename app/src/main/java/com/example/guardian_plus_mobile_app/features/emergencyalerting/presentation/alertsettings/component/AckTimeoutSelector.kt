package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AckTimeoutLimits

// A few sensible values inside the 15–300 s the platform accepts
private val TIMEOUT_OPTIONS = listOf(30, 60, 90, 120, 180, 300)

/** How long the primary contact has to acknowledge before the alert escalates (US11). */
@Composable
fun AckTimeoutSelector(
    modifier: Modifier = Modifier,
    selectedSeconds: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    // A value set elsewhere (e.g. 45 s through the API) still shows as selected
    val options = (TIMEOUT_OPTIONS + selectedSeconds)
        .filter { it in AckTimeoutLimits.MIN_SECONDS..AckTimeoutLimits.MAX_SECONDS }
        .distinct()
        .sorted()

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.settings_timeout),
            style = MaterialTheme.typography.titleSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { seconds ->
                FilterChip(
                    selected = seconds == selectedSeconds,
                    onClick = { onSelect(seconds) },
                    enabled = enabled,
                    label = { Text(text = stringResource(R.string.settings_timeout_value, seconds), style = MaterialTheme.typography.dataLabel) },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = enabled,
                        selected = seconds == selectedSeconds,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AckTimeoutSelectorPreview() {
    GuardianTheme(dynamicColor = false) {
        AckTimeoutSelector(selectedSeconds = 60, enabled = true, onSelect = {})
    }
}
