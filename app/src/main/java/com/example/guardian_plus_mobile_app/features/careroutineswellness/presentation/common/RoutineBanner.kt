package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Colored notice of the prototype: yellow for what is coming, orange for what needs attention, mint for info. */
@Composable
fun RoutineBanner(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    message: String,
    tone: RoutineTone,
    actionLabel: String? = null,
    isActionLoading: Boolean = false,
    onActionClick: () -> Unit = {}
) {
    val (container, content) = tone.colors()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = container,
        border = BorderStroke(1.dp, content.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = content,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize), color = content)
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = content.copy(alpha = 0.82f))
            }
            actionLabel?.let { label ->
                OutlinedButton(
                    onClick = onActionClick,
                    enabled = !isActionLoading,
                    shape = MaterialTheme.shapes.extraLarge,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .defaultMinSize(minHeight = 34.dp)
                ) {
                    if (isActionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = label, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

/** Muted one-line note with an icon, e.g. how the stock balance is calculated. */
@Composable
fun RoutineInfoLine(modifier: Modifier = Modifier, @DrawableRes iconRes: Int, text: String) {
    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            modifier = Modifier.padding(11.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview
@Composable
private fun RoutineBannerPreview() {
    GuardianTheme(dynamicColor = false) {
        RoutineBanner(
            iconRes = R.drawable.ic_triangle_alert,
            title = "Dosis pendiente: Omeprazol",
            message = "No se confirmó en 10 minutos. Se envió una segunda alerta a la pulsera.",
            tone = RoutineTone.ORANGE,
            actionLabel = "Revisar"
        )
    }
}
