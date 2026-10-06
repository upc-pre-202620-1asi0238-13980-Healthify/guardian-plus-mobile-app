package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

/** Green summary of the prototype: one glance tells if the care recipient is fine. */
@Composable
fun StatusHeroCard(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    allWithinRange: Boolean,
    hasLiveSignal: Boolean,
    updatedText: String,
    hasWristband: Boolean
) {
    val scheme = MaterialTheme.colorScheme
    // Orange instead of green as soon as one reading leaves its range
    val container = if (allWithinRange) scheme.primary else scheme.tertiary
    val onContainer = scheme.onPrimary
    val translucent = onContainer.copy(alpha = 0.12f)

    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = container) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(if (hasLiveSignal) R.string.home_status_online else R.string.home_status_offline),
                        style = MaterialTheme.typography.labelMedium,
                        color = onContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = stringResource(
                            if (allWithinRange) R.string.home_status_ok else R.string.home_status_attention,
                            careRecipientFirstName
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = onContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = stringResource(
                            if (allWithinRange) R.string.home_status_stable else R.string.home_status_unstable,
                            updatedText.lowercase()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(translucent, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = onContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(translucent, MaterialTheme.shapes.medium)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_watch),
                    contentDescription = null,
                    tint = onContainer,
                    modifier = Modifier.size(16.dp)
                )
                // Battery level is not reported by the platform, so the prototype's "68 %" is left out
                Text(
                    text = stringResource(if (hasWristband) R.string.home_wristband_connected else R.string.home_wristband_missing),
                    style = MaterialTheme.typography.bodySmall,
                    color = onContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatusHeroCardPreview() {
    GuardianTheme(dynamicColor = false) {
        StatusHeroCard(
            careRecipientFirstName = "Elena",
            allWithinRange = true,
            hasLiveSignal = true,
            updatedText = "Hace 2 min",
            hasWristband = true
        )
    }
}

@Preview
@Composable
private fun StatusHeroCardAttentionPreview() {
    GuardianTheme(dynamicColor = false) {
        StatusHeroCard(
            careRecipientFirstName = "Elena",
            allWithinRange = false,
            hasLiveSignal = false,
            updatedText = "Hace 5 min",
            hasWristband = true
        )
    }
}
