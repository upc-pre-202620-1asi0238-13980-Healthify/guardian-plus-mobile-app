package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Top bar of the screens opened from a list: back button, title, subtitle and an optional state chip. */
@Composable
fun DetailTopBar(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    statusLabel: String? = null,
    statusContainer: Color = Color.Unspecified,
    statusContent: Color = Color.Unspecified,
    onBackClick: () -> Unit
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedIconButton(
                onClick = onBackClick,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    contentDescription = stringResource(R.string.detail_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            statusLabel?.let {
                StatusChip(text = it, container = statusContainer, content = statusContent)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Preview
@Composable
private fun DetailTopBarPreview() {
    GuardianTheme(dynamicColor = false) {
        DetailTopBar(
            title = "Detalle de alerta",
            subtitle = "Elena Rojas",
            statusLabel = "Nueva",
            statusContainer = MaterialTheme.colorScheme.errorContainer,
            statusContent = MaterialTheme.colorScheme.onErrorContainer,
            onBackClick = {}
        )
    }
}
