package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity

/** "Todas · Críticas · Altas · Medias": the filters the platform supports (severity, not alert type). */
@Composable
fun SeverityFilterRow(
    modifier: Modifier = Modifier,
    selected: Severity?,
    onSelect: (Severity?) -> Unit
) {
    val options = listOf(
        null to R.string.history_filter_all,
        Severity.CRITICAL to R.string.history_filter_critical,
        Severity.HIGH to R.string.history_filter_high,
        Severity.MEDIUM to R.string.history_filter_medium
    )
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (severity, labelRes) ->
            FilterChip(
                selected = selected == severity,
                onClick = { onSelect(severity) },
                label = { Text(text = stringResource(labelRes), style = MaterialTheme.typography.titleSmall) },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == severity,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeverityFilterRowPreview() {
    GuardianTheme(dynamicColor = false) {
        SeverityFilterRow(modifier = Modifier.padding(16.dp), selected = null, onSelect = {})
    }
}
