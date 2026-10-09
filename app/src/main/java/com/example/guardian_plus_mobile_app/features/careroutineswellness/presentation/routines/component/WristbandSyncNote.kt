package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.routines.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.relativeTime
import java.time.Instant

/** "Pulsera sincronizada · Hace 1 min", from the last activity sample the wristband sent. */
@Composable
fun WristbandSyncNote(
    modifier: Modifier = Modifier,
    lastSyncAt: Instant,
    now: Instant
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_watch),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(R.string.routines_wristband_synced, relativeTime(lastSyncAt, now)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WristbandSyncNotePreview() {
    val now = Instant.parse("2026-10-08T18:35:00Z")
    GuardianTheme(dynamicColor = false) {
        WristbandSyncNote(lastSyncAt = now.minusSeconds(60), now = now)
    }
}
