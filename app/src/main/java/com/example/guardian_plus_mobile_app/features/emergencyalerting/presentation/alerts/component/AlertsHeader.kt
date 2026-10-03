package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerts.component

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Top bar of the Alerts section: avatar of the signed-in member, title and active alert count. */
@Composable
fun AlertsHeader(
    modifier: Modifier = Modifier,
    userInitials: String,
    careRecipientName: String,
    activeCount: Int,
    onSettingsClick: () -> Unit
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
                    text = careRecipientName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.alerts_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (activeCount > 0) {
                Text(
                    text = pluralStringResource(R.plurals.alerts_active_count, activeCount, activeCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.extraLarge)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            // "Configurar" of the report: emergency contacts and alert settings
            OutlinedIconButton(
                onClick = onSettingsClick,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.alerts_open_settings),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview
@Composable
private fun AlertsHeaderPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertsHeader(userInitials = "MR", careRecipientName = "Elena Rojas", activeCount = 3, onSettingsClick = {})
    }
}
