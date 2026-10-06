package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

enum class HealthTab {
    NOW,
    HISTORY
}

/** Segmented "Ahora · Historial" control of the prototype, same look as the Alerts one. */
@Composable
fun HealthTabRow(
    modifier: Modifier = Modifier,
    selectedTab: HealthTab,
    onTabSelected: (HealthTab) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(4.dp)
    ) {
        HealthTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Surface(
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = MaterialTheme.shapes.medium,
                color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (selected) 2.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(if (tab == HealthTab.NOW) R.string.health_tab_now else R.string.health_tab_history),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HealthTabRowPreview() {
    GuardianTheme(dynamicColor = false) {
        HealthTabRow(selectedTab = HealthTab.HISTORY, onTabSelected = {})
    }
}
