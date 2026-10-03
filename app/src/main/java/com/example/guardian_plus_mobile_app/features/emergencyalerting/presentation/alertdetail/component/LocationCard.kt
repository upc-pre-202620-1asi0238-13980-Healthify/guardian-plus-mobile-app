package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Where the person under care is (Mobility & Geofencing, simulated for now), with a shortcut to the map. */
@Composable
fun LocationCard(
    modifier: Modifier = Modifier,
    locationName: String,
    updatedAgo: String?,
    onViewMapClick: () -> Unit
) {
    InfoRowCard(
        modifier = modifier,
        iconRes = R.drawable.ic_map_pin,
        title = locationName,
        subtitle = updatedAgo?.let { stringResource(R.string.detail_location_updated, it) },
        actionLabel = stringResource(R.string.detail_view_map),
        onActionClick = onViewMapClick
    )
}

/** State of the wristband that raised the SOS (simulated until the device data is exposed). */
@Composable
fun DeviceCard(modifier: Modifier = Modifier, deviceSummary: String) {
    InfoRowCard(
        modifier = modifier,
        iconRes = R.drawable.ic_watch,
        title = stringResource(R.string.detail_device_name),
        subtitle = deviceSummary
    )
}

@Composable
private fun InfoRowCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String?,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                subtitle?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (actionLabel != null) {
                TextButton(onClick = onActionClick) {
                    Text(text = actionLabel, style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationCardPreview() {
    GuardianTheme(dynamicColor = false) {
        LocationCard(
            modifier = Modifier.padding(16.dp),
            locationName = "Dormitorio · Casa de Elena",
            updatedAgo = "hace 30 s",
            onViewMapClick = {}
        )
    }
}
