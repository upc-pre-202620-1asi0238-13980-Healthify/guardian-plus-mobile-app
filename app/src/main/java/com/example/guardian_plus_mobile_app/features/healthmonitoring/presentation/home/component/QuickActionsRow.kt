package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** "Llamar · Videollamada · Ubicación", the three shortcuts under the status card. */
@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier,
    onCallClick: () -> Unit,
    onVideoClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // Min intrinsic height lets the dividers stretch to the tallest action
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            QuickAction(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_phone,
                label = stringResource(R.string.home_action_call),
                onClick = onCallClick
            )
            VerticalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
            QuickAction(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_video,
                label = stringResource(R.string.home_action_video),
                onClick = onVideoClick
            )
            VerticalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
            QuickAction(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_map_pin,
                label = stringResource(R.string.home_action_location),
                onClick = onLocationClick
            )
        }
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun QuickActionsRowPreview() {
    GuardianTheme(dynamicColor = false) {
        QuickActionsRow(onCallClick = {}, onVideoClick = {}, onLocationClick = {})
    }
}
