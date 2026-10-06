package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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

/** "Buenos días, María · Todo bajo control": greeting of the signed-in member and the bell to the alerts. */
@Composable
fun HomeHeader(
    modifier: Modifier = Modifier,
    userInitials: String,
    userFirstName: String,
    careRecipientFirstName: String,
    allWithinRange: Boolean,
    hour: Int,
    onBellClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitials,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    when {
                        hour < 12 -> R.string.home_greeting_morning
                        hour < 19 -> R.string.home_greeting_afternoon
                        else -> R.string.home_greeting_evening
                    },
                    userFirstName
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (allWithinRange) {
                    stringResource(R.string.home_title_ok)
                } else {
                    stringResource(R.string.home_title_attention, careRecipientFirstName)
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        // The unread count of the prototype lives in Emergency & Alerting, so the bell only opens that tab
        OutlinedIconButton(
            onClick = onBellClick,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = stringResource(R.string.home_open_alerts),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview() {
    GuardianTheme(dynamicColor = false) {
        HomeHeader(
            userInitials = "MR",
            userFirstName = "María",
            careRecipientFirstName = "Elena",
            allWithinRange = true,
            hour = 9,
            onBellClick = {}
        )
    }
}
