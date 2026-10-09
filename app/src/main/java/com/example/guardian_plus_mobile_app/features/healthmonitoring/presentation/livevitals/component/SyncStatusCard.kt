package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.livevitals.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip

/** "Sincronización activa": whether the last refresh reached the platform. */
@Composable
fun SyncStatusCard(modifier: Modifier = Modifier, synced: Boolean) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = scheme.surface,
        border = BorderStroke(1.dp, scheme.outline)
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.health_sync_title), style = MaterialTheme.typography.titleSmall, color = scheme.onSurface)
                Text(
                    text = stringResource(R.string.health_sync_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, end = 12.dp)
                )
            }
            StatusChip(
                text = stringResource(if (synced) R.string.health_sync_done else R.string.health_sync_retrying),
                container = if (synced) scheme.surfaceVariant else scheme.noticeContainer,
                content = if (synced) scheme.onSurfaceVariant else scheme.onNoticeContainer
            )
        }
    }
}

@Preview
@Composable
private fun SyncStatusCardPreview() {
    GuardianTheme(dynamicColor = false) {
        SyncStatusCard(synced = true)
    }
}
